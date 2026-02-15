package dev.oblivion.client.module.render;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import dev.oblivion.client.setting.impl.EnumSetting;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class Fullbright extends Module {

    public enum Mode {
        GAMMA,
        POTION
    }

    private final EnumSetting<Mode> mode = settings.getDefaultGroup().add(
            new EnumSetting.Builder<Mode>().name("Mode").description("Fullbright method").defaultValue(Mode.GAMMA).build()
    );
    private final DoubleSetting brightness = settings.getDefaultGroup().add(
            new DoubleSetting.Builder().name("Brightness").description("Gamma brightness level").defaultValue(16.0).min(1.0).max(16.0).build()
    );

    private double previousGamma;

    public Fullbright() {
        super("Fullbright", "Makes everything fully bright", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        previousGamma = mc.options.getGamma().getValue();

        if (mode.get() == Mode.GAMMA) {
            mc.options.getGamma().setValue(brightness.get());
        } else if (mode.get() == Mode.POTION) {
            if (mc.player != null) {
                mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
            }
        }
    }

    @Override
    protected void onDisable() {
        mc.options.getGamma().setValue(previousGamma);

        if (mode.get() == Mode.POTION && mc.player != null) {
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }

    public Mode getMode() {
        return mode.get();
    }

    public double getBrightness() {
        return brightness.get();
    }
}
