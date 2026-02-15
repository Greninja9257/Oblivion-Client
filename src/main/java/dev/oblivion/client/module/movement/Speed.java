package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import dev.oblivion.client.setting.impl.EnumSetting;
import net.minecraft.util.math.Vec3d;

public class Speed extends Module {

    public enum Mode { VANILLA, STRAFE, BHOP }

    private final DoubleSetting speed = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Speed")
            .description("Speed multiplier.")
            .defaultValue(1.5)
            .min(1.0)
            .max(5.0)
            .build()
    );

    private final EnumSetting<Mode> mode = settings.getDefaultGroup().add(
        new EnumSetting.Builder<Mode>()
            .name("Mode")
            .description("Speed mode.")
            .defaultValue(Mode.VANILLA)
            .build()
    );

    public Speed() {
        super("Speed", "Increases your movement speed.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;
        if (mc.player.isSneaking()) return;

        double multiplier = speed.get();
        double forward = mc.player.input.movementForward;
        double sideways = mc.player.input.movementSideways;
        boolean moving = forward != 0 || sideways != 0;

        switch (mode.get()) {
            case VANILLA -> {
                if (!mc.player.isOnGround()) return;
                Vec3d vel = mc.player.getVelocity();
                mc.player.setVelocity(vel.x * multiplier, vel.y, vel.z * multiplier);
            }
            case STRAFE -> {
                if (!moving) return;
                applyStrafeVelocity(multiplier, false);
            }
            case BHOP -> {
                if (!moving) return;

                if (mc.player.isOnGround()) {
                    mc.player.jump();
                }

                applyStrafeVelocity(multiplier * 1.05, true);
            }
        }
    }

    private void applyStrafeVelocity(double multiplier, boolean preserveMomentum) {
        float yaw = mc.player.getYaw();
        double forward = mc.player.input.movementForward;
        double sideways = mc.player.input.movementSideways;

        double angle = Math.toRadians(yaw);
        if (forward != 0) {
            if (sideways > 0) angle -= (forward > 0 ? 45 : -45);
            else if (sideways < 0) angle += (forward > 0 ? 45 : -45);
            if (forward > 0) angle -= Math.PI;
        } else {
            if (sideways > 0) angle -= Math.PI / 2;
            else if (sideways < 0) angle += Math.PI / 2;
        }

        double baseSpeed = 0.2873 * multiplier;
        Vec3d current = mc.player.getVelocity();
        double nextX = -Math.sin(angle) * baseSpeed;
        double nextZ = Math.cos(angle) * baseSpeed;

        if (preserveMomentum && !mc.player.isOnGround()) {
            nextX = (current.x * 0.75) + (nextX * 0.25);
            nextZ = (current.z * 0.75) + (nextZ * 0.25);
        }

        mc.player.setVelocity(nextX, current.y, nextZ);
    }
}
