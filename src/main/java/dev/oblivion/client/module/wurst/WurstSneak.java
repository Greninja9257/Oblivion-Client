package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstSneak extends Module {
    public WurstSneak() {
        super("Sneak", "Continuously sneaks.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player != null) {
            mc.options.sneakKey.setPressed(true);
        }
    }

    @Override
    protected void onDisable() {
        if (mc.options != null) {
            mc.options.sneakKey.setPressed(false);
        }
    }
}
