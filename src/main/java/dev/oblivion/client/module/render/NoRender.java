package dev.oblivion.client.module.render;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;

/**
 * Stores settings for cancelling various render effects.
 * Actual rendering cancellation is performed through mixins that check
 * whether this module is enabled and which settings are active.
 */
public class NoRender extends Module {

    private final BoolSetting hurtCam = settings.getDefaultGroup().add(
            new BoolSetting.Builder().name("HurtCam").description("Remove screen shake when hurt").defaultValue(true).build()
    );
    private final BoolSetting fire = settings.getDefaultGroup().add(
            new BoolSetting.Builder().name("Fire").description("Remove fire overlay on screen").defaultValue(true).build()
    );
    private final BoolSetting pumpkin = settings.getDefaultGroup().add(
            new BoolSetting.Builder().name("Pumpkin").description("Remove pumpkin overlay").defaultValue(true).build()
    );
    private final BoolSetting fog = settings.getDefaultGroup().add(
            new BoolSetting.Builder().name("Fog").description("Remove fog rendering").defaultValue(true).build()
    );
    private final BoolSetting particles = settings.getDefaultGroup().add(
            new BoolSetting.Builder().name("Particles").description("Remove all particles").defaultValue(false).build()
    );
    private final BoolSetting totemAnimation = settings.getDefaultGroup().add(
            new BoolSetting.Builder().name("TotemAnimation").description("Remove totem of undying animation").defaultValue(true).build()
    );

    public NoRender() {
        super("NoRender", "Cancels various render effects", Category.RENDER);
    }

    public boolean hurtCam() {
        return isEnabled() && hurtCam.get();
    }

    public boolean noFire() {
        return isEnabled() && fire.get();
    }

    public boolean noPumpkin() {
        return isEnabled() && pumpkin.get();
    }

    public boolean noFog() {
        return isEnabled() && fog.get();
    }

    public boolean noParticles() {
        return isEnabled() && particles.get();
    }

    public boolean noTotemAnimation() {
        return isEnabled() && totemAnimation.get();
    }
}
