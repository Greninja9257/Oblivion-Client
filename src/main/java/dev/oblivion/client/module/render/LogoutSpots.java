package dev.oblivion.client.module.render;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LogoutSpots extends Module {

    private final Map<String, Vec3d> logoutPositions = new HashMap<>();
    private final Set<String> trackedPlayers = new HashSet<>();

    public LogoutSpots() {
        super("LogoutSpots", "Shows where other players logged out.", Category.RENDER);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        Set<String> currentPlayers = new HashSet<>();
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            String name = player.getName().getString();
            currentPlayers.add(name);

            if (!trackedPlayers.contains(name)) {
                trackedPlayers.add(name);
            }
            // Remove logout spot when player comes back
            logoutPositions.remove(name);
        }

        // Any player that was tracked but is no longer in world has logged out
        for (String name : trackedPlayers) {
            if (!currentPlayers.contains(name) && !logoutPositions.containsKey(name)) {
                // They were in the world last tick but not this tick
            }
        }

        // Update tracking: check who left
        Set<String> left = new HashSet<>(trackedPlayers);
        left.removeAll(currentPlayers);
        // We can't get their last position once they're gone, so track positions continuously
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            trackedPlayers.add(player.getName().getString());
        }
    }

    public Map<String, Vec3d> getLogoutPositions() { return logoutPositions; }

    @Override
    protected void onDisable() {
        logoutPositions.clear();
        trackedPlayers.clear();
    }
}
