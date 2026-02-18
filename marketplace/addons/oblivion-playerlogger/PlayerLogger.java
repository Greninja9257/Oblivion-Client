import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerLogger extends Module {

    private final IntSetting logInterval = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Log Interval")
            .description("Seconds between logging each player's position.")
            .defaultValue(30)
            .range(5, 300)
            .build()
    );

    private final BoolSetting logCoords = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Log Coords")
            .description("Log player coordinates.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting logEquipment = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Log Equipment")
            .description("Log what armor and items players are holding.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting logJoinLeave = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Log Join/Leave")
            .description("Log when players enter and leave render distance.")
            .defaultValue(true)
            .build()
    );

    private static final Path LOG_FILE = Path.of("run", "oblivion-client", "player-log.txt");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Set<String> previouslyVisible = new HashSet<>();
    private final Map<String, Long> lastLogTime = new HashMap<>();
    private int tickCounter = 0;

    public PlayerLogger() {
        super("PlayerLogger", "Logs player coords, gear, and join/leave to a file.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        previouslyVisible.clear();
        lastLogTime.clear();
        tickCounter = 0;
        writeLog("--- PlayerLogger enabled ---");
    }

    @Override
    protected void onDisable() {
        writeLog("--- PlayerLogger disabled ---");
        previouslyVisible.clear();
        lastLogTime.clear();
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        tickCounter++;
        Set<String> currentlyVisible = new HashSet<>();

        for (var entity : mc.world.getEntities()) {
            if (!(entity instanceof PlayerEntity player)) continue;
            if (player == mc.player) continue;

            String name = player.getName().getString();
            currentlyVisible.add(name);

            // Join detection
            if (logJoinLeave.get() && !previouslyVisible.contains(name)) {
                BlockPos pos = player.getBlockPos();
                writeLog("[JOIN] " + name + " entered render distance at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
            }

            // Periodic coordinate + equipment logging
            long now = System.currentTimeMillis();
            long intervalMs = logInterval.get() * 1000L;
            Long lastTime = lastLogTime.get(name);
            if (lastTime == null || (now - lastTime) >= intervalMs) {
                lastLogTime.put(name, now);
                StringBuilder sb = new StringBuilder();
                sb.append("[TRACK] ").append(name);

                if (logCoords.get()) {
                    BlockPos pos = player.getBlockPos();
                    sb.append(" @ ").append(pos.getX()).append(", ").append(pos.getY()).append(", ").append(pos.getZ());
                    sb.append(" (dist: ").append(String.format("%.0f", mc.player.distanceTo(player))).append(")");
                }

                if (logEquipment.get()) {
                    sb.append(" | ");
                    sb.append("Hand: ").append(itemName(player.getMainHandStack()));
                    sb.append(", Head: ").append(itemName(player.getEquippedStack(EquipmentSlot.HEAD)));
                    sb.append(", Chest: ").append(itemName(player.getEquippedStack(EquipmentSlot.CHEST)));
                    sb.append(", Legs: ").append(itemName(player.getEquippedStack(EquipmentSlot.LEGS)));
                    sb.append(", Feet: ").append(itemName(player.getEquippedStack(EquipmentSlot.FEET)));
                }

                writeLog(sb.toString());
            }
        }

        // Leave detection
        if (logJoinLeave.get()) {
            for (String name : previouslyVisible) {
                if (!currentlyVisible.contains(name)) {
                    writeLog("[LEAVE] " + name + " left render distance");
                    lastLogTime.remove(name);
                }
            }
        }

        previouslyVisible.clear();
        previouslyVisible.addAll(currentlyVisible);
    }

    private String itemName(ItemStack stack) {
        if (stack.isEmpty()) return "empty";
        return stack.getItem().toString();
    }

    private void writeLog(String line) {
        try {
            Files.createDirectories(LOG_FILE.getParent());
            String timestamp = LocalDateTime.now().format(TIME_FMT);
            String entry = "[" + timestamp + "] " + line + "\n";
            Files.writeString(LOG_FILE, entry, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {}
    }
}
