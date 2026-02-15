package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstAntiWaterPush extends Module {
    public WurstAntiWaterPush() {
        super("AntiWaterPush", "Prevents horizontal water current push.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        if (!mc.player.isTouchingWater()) return;

        mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
    }
}
