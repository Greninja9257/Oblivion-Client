package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstAntiEntityPush extends Module {
    public WurstAntiEntityPush() {
        super("AntiEntityPush", "Reduces horizontal velocity spikes from entity collisions.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        if (mc.player.horizontalCollision) {
            mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
        }
    }
}
