package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstLiquids extends Module {
    public WurstLiquids() {
        super("Liquids", "Lets you move smoothly on liquid surfaces.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        if ((mc.player.isTouchingWater() || mc.player.isInLava()) && mc.player.getVelocity().y < 0) {
            mc.player.setVelocity(mc.player.getVelocity().x, 0.08, mc.player.getVelocity().z);
        }
    }
}
