package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstAutoWalk extends Module {
    public WurstAutoWalk() {
        super("AutoWalk", "Continuously walks forward.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player != null) {
            mc.options.forwardKey.setPressed(true);
        }
    }

    @Override
    protected void onDisable() {
        if (mc.options != null) {
            mc.options.forwardKey.setPressed(false);
        }
    }
}
