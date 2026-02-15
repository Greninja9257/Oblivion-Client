package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;

public final class WurstNoClip extends Module {
    private final DoubleSetting speed = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Speed").description("NoClip fly speed").defaultValue(0.35).range(0.05, 1.5).build()
    );

    public WurstNoClip() {
        super("NoClip", "Allows movement through blocks.", Category.MOVEMENT);
    }

    @Override
    protected void onEnable() {
        if (mc.player != null) mc.player.noClip = true;
    }

    @Override
    protected void onDisable() {
        if (mc.player != null) mc.player.noClip = false;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        mc.player.noClip = true;

        double y = 0;
        if (mc.options.jumpKey.isPressed()) y += speed.get();
        if (mc.options.sneakKey.isPressed()) y -= speed.get();

        double forward = mc.player.input.movementForward;
        double strafe = mc.player.input.movementSideways;
        float yaw = mc.player.getYaw();
        double rad = Math.toRadians(yaw);

        double x = (-Math.sin(rad) * forward + Math.cos(rad) * strafe) * speed.get();
        double z = ( Math.cos(rad) * forward + Math.sin(rad) * strafe) * speed.get();
        mc.player.setVelocity(x, y, z);
    }
}
