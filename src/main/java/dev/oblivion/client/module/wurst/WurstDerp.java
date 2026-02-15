package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstDerp extends Module {
    public WurstDerp() {
        super("Derp", "Randomly jitters yaw/pitch.", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        mc.player.setYaw(mc.player.getYaw() + (float) ((Math.random() - 0.5) * 90));
        mc.player.setPitch((float) Math.max(-89, Math.min(89, mc.player.getPitch() + (Math.random() - 0.5) * 60)));
    }
}
