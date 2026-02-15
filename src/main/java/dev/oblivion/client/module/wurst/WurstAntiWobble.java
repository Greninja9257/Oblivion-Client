package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstAntiWobble extends Module {
    public WurstAntiWobble() {
        super("AntiWobble", "Stabilizes view angles to reduce camera wobble.", Category.RENDER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        float yaw = mc.player.getYaw();
        float pitch = mc.player.getPitch();
        mc.player.setYaw(Math.round(yaw * 2f) / 2f);
        mc.player.setPitch(Math.round(pitch * 2f) / 2f);
    }
}
