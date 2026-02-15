package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import net.minecraft.entity.effect.StatusEffects;

public final class WurstNoFog extends Module {
    private final BoolSetting clearBlindness = settings.getDefaultGroup().add(
        new BoolSetting.Builder().name("Clear Blindness").description("Removes blindness effect each tick").defaultValue(true).build()
    );

    private final BoolSetting clearDarkness = settings.getDefaultGroup().add(
        new BoolSetting.Builder().name("Clear Darkness").description("Removes darkness effect each tick").defaultValue(true).build()
    );

    public WurstNoFog() {
        super("NoFog", "Reduces fog by clearing vision-limiting effects.", Category.RENDER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        if (clearBlindness.get()) {
            mc.player.removeStatusEffect(StatusEffects.BLINDNESS);
        }
        if (clearDarkness.get()) {
            mc.player.removeStatusEffect(StatusEffects.DARKNESS);
        }
    }
}
