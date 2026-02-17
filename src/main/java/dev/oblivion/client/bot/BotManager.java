package dev.oblivion.client.bot;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.bot.task.*;

import java.util.*;
import java.util.concurrent.*;

public class BotManager {
    private final Map<String, Bot> bots = new ConcurrentHashMap<>();
    private final BotConfig config = new BotConfig();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2, r -> {
        Thread t = new Thread(r, "oblivion-bot-tick");
        t.setDaemon(true);
        return t;
    });
    private final Random random = new Random();

    public void init() {
        // No external process to start
    }

    public void shutdown() {
        disconnectAll();
        executor.shutdownNow();
    }

    // --- Spawn / Disconnect ---

    public List<String> spawnSwarm(String host, int port, int count,
                                    boolean randomNames, String namePrefix, int randomLength, String fixedName) {
        List<String> pendingNames = config.consumePendingNames();
        List<String> spawned = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String username;
            if (i < pendingNames.size()) {
                username = pendingNames.get(i);
            } else if (randomNames) {
                username = randomName(namePrefix, randomLength);
            } else {
                username = fixedName + (i + 1);
            }
            if (bots.containsKey(username)) continue;

            Bot bot = new Bot(username, config);
            bot.setOnDisconnect(() -> bots.remove(username));
            bots.put(username, bot);

            String finalHost = host;
            int finalPort = port;
            executor.submit(() -> {
                try {
                    bot.connect(finalHost, finalPort);
                    bot.setTickFuture(executor.scheduleAtFixedRate(bot::tick, 50, 50, TimeUnit.MILLISECONDS));
                } catch (Exception e) {
                    OblivionClient.LOGGER.warn("Bot {} failed to connect: {}", username, e.getMessage());
                    bots.remove(username);
                }
            });
            spawned.add(username);
        }
        return spawned;
    }

    public int disconnectAll() {
        int count = bots.size();
        for (Bot bot : bots.values()) {
            try { bot.disconnect(); } catch (Exception ignored) {}
        }
        bots.clear();
        return count;
    }

    // --- Bot selection ---

    private List<Bot> selectBots(int count) {
        List<Bot> all = new ArrayList<>(bots.values());
        return all.subList(0, Math.min(count, all.size()));
    }

    // --- Task dispatch ---

    public void startFollow(int count, String target, double distance) {
        for (Bot bot : selectBots(count)) bot.startTask(new FollowTask(target, distance));
    }

    public void stopFollow(int count) {
        for (Bot bot : selectBots(count)) bot.stopTask();
    }

    public void startMine(int count, String blockId, int maxBlocks) {
        for (Bot bot : selectBots(count)) bot.startTask(new MineTask(blockId, maxBlocks));
    }

    public void stopMine(int count) {
        for (Bot bot : selectBots(count)) bot.stopTask();
    }

    public void startFarm(int count, String crop) {
        for (Bot bot : selectBots(count)) bot.startTask(new FarmTask(crop));
    }

    public void stopFarm(int count) {
        for (Bot bot : selectBots(count)) bot.stopTask();
    }

    public void startGuard(int count, String target) {
        for (Bot bot : selectBots(count)) bot.startTask(new GuardTask(target));
    }

    public void stopGuard(int count) {
        for (Bot bot : selectBots(count)) bot.stopTask();
    }

    public void startCollect(int count, double radius) {
        for (Bot bot : selectBots(count)) bot.startTask(new CollectTask(radius));
    }

    public void stopCollect(int count) {
        for (Bot bot : selectBots(count)) bot.stopTask();
    }

    public void deposit(int count, int x, int y, int z) {
        for (Bot bot : selectBots(count)) bot.startTask(new DepositTask(x, y, z));
    }

    public void chatSend(int count, String message) {
        for (Bot bot : selectBots(count)) bot.chat(message);
    }

    public void chatSpamStart(int count, String message, int delayMs) {
        for (Bot bot : selectBots(count)) bot.startTask(new ChatSpamTask(message, delayMs));
    }

    public void chatSpamStop(int count) {
        for (Bot bot : selectBots(count)) bot.stopTask();
    }

    public void stopAllTasks(int count) {
        for (Bot bot : selectBots(count)) bot.stopTask();
    }

    // --- Config ---

    public void setAutoRegister(boolean enabled, String password, String registerFormat, String loginFormat) {
        if (enabled) {
            config.setAutoRegister(new BotConfig.AutoRegister(password, registerFormat, loginFormat));
        } else {
            config.setAutoRegister(null);
        }
    }

    public void setNames(List<String> names) {
        config.setPendingNames(names);
    }

    // --- Utility ---

    public String randomName(String prefix, int randomLength) {
        String safePrefix = (prefix == null || prefix.isBlank()) ? "bot" : prefix;
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(safePrefix);
        for (int i = 0; i < Math.max(3, randomLength); i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public int getBotCount() { return bots.size(); }
    public Set<String> getBotNames() { return Set.copyOf(bots.keySet()); }
    public BotConfig getConfig() { return config; }
}
