package dev.oblivion.client.bot;

import dev.oblivion.client.bot.movement.BotMovement;
import dev.oblivion.client.bot.task.BotTask;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.event.session.ConnectedEvent;
import org.geysermc.mcprotocollib.network.event.session.DisconnectedEvent;
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter;
import org.geysermc.mcprotocollib.network.factory.ClientNetworkSessionFactory;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.network.session.ClientNetworkSession;
import org.geysermc.mcprotocollib.protocol.MinecraftProtocol;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerChatPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatPacket;

import java.util.BitSet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Bot {
    private final String username;
    private final BotConfig config;
    private final BotWorldTracker tracker;
    private final BotInventoryTracker inventory;
    private final BotMovement movement;
    private final AtomicReference<BotState> state = new AtomicReference<>(BotState.DISCONNECTED);
    private final AtomicReference<BotTask> currentTask = new AtomicReference<>(null);
    private final AtomicInteger actionSequence = new AtomicInteger();

    private volatile Session session;
    private volatile ScheduledFuture<?> tickFuture;
    private volatile Runnable onDisconnect;

    public Bot(String username, BotConfig config) {
        this.username = username;
        this.config = config;
        this.tracker = new BotWorldTracker();
        this.inventory = new BotInventoryTracker();
        this.movement = new BotMovement(this);
    }

    public void connect(String host, int port) {
        if (!state.compareAndSet(BotState.DISCONNECTED, BotState.CONNECTING)) return;

        MinecraftProtocol protocol = new MinecraftProtocol(username);
        ClientNetworkSession s = ClientNetworkSessionFactory.factory()
            .setAddress(host, port)
            .setProtocol(protocol)
            .create();

        s.addListener(tracker);
        s.addListener(inventory);
        s.addListener(new SessionAdapter() {
            @Override
            public void connected(ConnectedEvent event) {
                state.set(BotState.CONNECTED);
            }

            @Override
            public void packetReceived(Session session, Packet packet) {
                handleAutoRegister(packet);
            }

            @Override
            public void disconnected(DisconnectedEvent event) {
                state.set(BotState.DISCONNECTED);
                stopTask();
                cancelTick();
                tracker.clear();
                inventory.clear();
                actionSequence.set(0);
                if (onDisconnect != null) onDisconnect.run();
            }
        });

        this.session = s;
        s.connect(false);
    }

    public void disconnect() {
        if (state.get() == BotState.DISCONNECTED) return;
        stopTask();
        cancelTick();
        Session s = this.session;
        if (s != null && s.isConnected()) {
            s.disconnect("Disconnected");
        }
        state.set(BotState.DISCONNECTED);
    }

    public void tick() {
        if (state.get() != BotState.CONNECTED || session == null) return;
        movement.tick();
        BotTask task = currentTask.get();
        if (task != null) task.tick(this);
    }

    public void startTask(BotTask task) {
        stopTask();
        currentTask.set(task);
        task.start(this);
    }

    public void stopTask() {
        BotTask old = currentTask.getAndSet(null);
        if (old != null) old.stop(this);
        movement.clearGoal();
    }

    public void chat(String message) {
        if (session == null || state.get() != BotState.CONNECTED) return;
        if (message.startsWith("/")) {
            session.send(new ServerboundChatCommandPacket(message.substring(1)));
        } else {
            session.send(new ServerboundChatPacket(
                message, System.currentTimeMillis(), 0L, new byte[0], 0, new BitSet()
            ));
        }
    }

    public int nextActionSequence() {
        return actionSequence.incrementAndGet();
    }

    public void sendPacket(Packet packet) {
        Session s = this.session;
        if (s != null && s.isConnected()) {
            s.send(packet);
        }
    }

    private void handleAutoRegister(Packet packet) {
        BotConfig.AutoRegister ar = config.getAutoRegister();
        if (ar == null) return;

        String msg = null;
        if (packet instanceof ClientboundSystemChatPacket sys) {
            msg = String.valueOf(sys.getContent());
        } else if (packet instanceof ClientboundPlayerChatPacket chat) {
            msg = chat.getContent();
        }

        if (msg == null) return;
        String lower = msg.toLowerCase();
        if (lower.contains("/register")) {
            chat(ar.registerFormat().replace("{p}", ar.password()));
        }
        if (lower.contains("/login")) {
            chat(ar.loginFormat().replace("{p}", ar.password()));
        }
    }

    void cancelTick() {
        if (tickFuture != null) {
            tickFuture.cancel(false);
            tickFuture = null;
        }
    }

    public void setTickFuture(ScheduledFuture<?> future) {
        this.tickFuture = future;
    }

    public void setOnDisconnect(Runnable onDisconnect) {
        this.onDisconnect = onDisconnect;
    }

    public String getUsername() { return username; }
    public BotState getState() { return state.get(); }
    public BotWorldTracker getTracker() { return tracker; }
    public BotInventoryTracker getInventory() { return inventory; }
    public BotMovement getMovement() { return movement; }
    public BotConfig getConfig() { return config; }
    public Session getSession() { return session; }
}
