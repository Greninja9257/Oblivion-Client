package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstTired extends Module {
    public WurstTired() {
        super("Tired", "Intentionally slows movement for stealth-like behavior.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        mc.player.setVelocity(mc.player.getVelocity().x * 0.55, mc.player.getVelocity().y, mc.player.getVelocity().z * 0.55);
    }
}
