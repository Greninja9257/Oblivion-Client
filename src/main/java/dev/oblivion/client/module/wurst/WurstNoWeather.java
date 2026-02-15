package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstNoWeather extends Module {
    public WurstNoWeather() {
        super("NoWeather", "Suppresses rain and thunder client-side.", Category.RENDER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.world == null) return;
        mc.world.setRainGradient(0f);
        mc.world.setThunderGradient(0f);
    }
}
