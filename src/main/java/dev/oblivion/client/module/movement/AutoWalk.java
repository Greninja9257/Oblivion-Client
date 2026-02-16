package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public class AutoWalk extends Module {

    public AutoWalk() {
        super("AutoWalk", "Automatically walks forward.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null) return;
        mc.options.forwardKey.setPressed(true);
    }

    @Override
    protected void onDisable() {
        if (mc.player != null) {
            mc.options.forwardKey.setPressed(false);
        }
    }
}
