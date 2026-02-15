package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;

public class Step extends Module {

    private final DoubleSetting height = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Height")
            .description("Maximum step height in blocks.")
            .defaultValue(1.0)
            .min(0.5)
            .max(5.0)
            .build()
    );

    public Step() {
        super("Step", "Allows you to step up blocks instantly.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        // Handled by MixinEntity#getStepHeight.
    }

    /**
     * Returns the configured step height. Used by the Entity step height mixin
     * to override the default step height when this module is enabled.
     */
    public float getStepHeight() {
        return height.get().floatValue();
    }
}
