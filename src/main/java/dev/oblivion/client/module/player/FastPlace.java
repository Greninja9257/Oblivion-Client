package dev.oblivion.client.module.player;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;

public class FastPlace extends Module {

    private final IntSetting delay = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Delay")
            .description("Item use cooldown in ticks (0 = no delay).")
            .defaultValue(0)
            .range(0, 4)
            .build()
    );

    public FastPlace() {
        super("FastPlace", "Removes the delay between placing blocks or using items.", Category.PLAYER);
    }

    public int getDelay() {
        return delay.get();
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        // Actual field access is done via mixin accessor on MinecraftClient.
        // The mixin sets mc.itemUseCooldown to the delay value each tick.
        // This module exposes getDelay() for the mixin to reference.
    }
}
