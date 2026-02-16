package dev.oblivion.client.module.render;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import dev.oblivion.client.setting.impl.EnumSetting;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class Fullbright extends Module {

    public enum Mode {
        NIGHT_VISION,
        GAMMA
    }

    private final EnumSetting<Mode> mode = settings.getDefaultGroup().add(
            new EnumSetting.Builder<Mode>().name("Mode").description("Fullbright method").defaultValue(Mode.NIGHT_VISION).build()
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
        applyMode();
    }

    @Override
    protected void onDisable() {
        mc.options.getGamma().setValue(previousGamma);

        if (mc.player != null) {
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        double targetGamma = Math.max(brightness.get(), 100.0);
        if (mc.options.getGamma().getValue() != targetGamma) {
            mc.options.getGamma().setValue(targetGamma);
        }

        if (mc.player == null) return;

        if (mode.get() == Mode.NIGHT_VISION) {
            // Re-apply silently to keep true fullbright even after milk/death/effect clears.
            StatusEffectInstance current = mc.player.getStatusEffect(StatusEffects.NIGHT_VISION);
            if (current == null || current.getDuration() < 220) {
                mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 600, 0, false, false, false));
            }
        } else {
            if (mc.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
            }
        }
    }

    private void applyMode() {
        double targetGamma = Math.max(brightness.get(), 100.0);
        mc.options.getGamma().setValue(targetGamma);

        if (mc.player == null) return;

        if (mode.get() == Mode.NIGHT_VISION) {
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 600, 0, false, false, false));
        } else {
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
