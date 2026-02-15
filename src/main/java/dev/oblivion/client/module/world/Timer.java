package dev.oblivion.client.module.world;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;

public class Timer extends Module {

    private final DoubleSetting speed = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Speed")
            .description("Game speed multiplier (1.0 = normal).")
            .defaultValue(2.0)
            .range(0.1, 10.0)
            .build()
    );

    public Timer() {
        super("Timer", "Changes the game tick speed.", Category.WORLD);
    }

    /**
     * Returns the timer speed multiplier.
     * Used by MixinRenderTickCounter to modify the tick rate.
     */
    public float getMultiplier() {
        return speed.get().floatValue();
    }
}
