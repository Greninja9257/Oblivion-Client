package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstMileyCyrus extends Module {
    public WurstMileyCyrus() {
        super("MileyCyrus", "Rapidly spins your yaw around.", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player != null) {
            mc.player.setYaw(mc.player.getYaw() + 35f);
        }
    }
}
