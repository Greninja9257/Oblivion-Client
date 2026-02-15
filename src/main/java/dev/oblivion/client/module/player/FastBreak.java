package dev.oblivion.client.module.player;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public class FastBreak extends Module {

    public FastBreak() {
        super("FastBreak", "Removes the block breaking cooldown.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.interactionManager == null) return;
        // Actual field access is done via mixin accessor on ClientPlayerInteractionManager.
        // The mixin sets blockBreakingCooldown to 0 each tick.
    }
}
