package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstAutoRespawn extends Module {
    public WurstAutoRespawn() {
        super("AutoRespawn", "Automatically respawns after death.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player != null && mc.player.isDead()) {
            mc.player.requestRespawn();
        }
    }
}
