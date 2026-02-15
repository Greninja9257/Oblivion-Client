package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstHeadRoll extends Module {
    private int tick;

    public WurstHeadRoll() {
        super("HeadRoll", "Oscillates pitch for head-roll visual effect.", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        tick++;
        float pitch = (float) (Math.sin(tick * 0.25) * 45.0);
        mc.player.setPitch(pitch);
    }
}
