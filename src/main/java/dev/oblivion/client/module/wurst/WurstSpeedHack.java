package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;

public final class WurstSpeedHack extends Module {
    private final DoubleSetting multiplier = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Multiplier").description("Speed multiplier").defaultValue(1.55).range(1.0, 5.0).build()
    );

    public WurstSpeedHack() {
        super("SpeedHack", "Increases movement speed with strafe support.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        if (mc.player.input.movementForward == 0 && mc.player.input.movementSideways == 0) return;

        double yaw = Math.toRadians(mc.player.getYaw());
        double forward = mc.player.input.movementForward;
        double strafe = mc.player.input.movementSideways;

        double x = (-Math.sin(yaw) * forward + Math.cos(yaw) * strafe) * 0.2873 * multiplier.get();
        double z = ( Math.cos(yaw) * forward + Math.sin(yaw) * strafe) * 0.2873 * multiplier.get();

        mc.player.setVelocity(x, mc.player.getVelocity().y, z);
    }
}
