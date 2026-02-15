package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstNoFireOverlay extends Module {
    public WurstNoFireOverlay() {
        super("NoFireOverlay", "Extinguishes fire to remove screen fire overlay.", Category.RENDER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        if (mc.player.isOnFire()) {
            mc.player.setFireTicks(0);
        }
    }
}
