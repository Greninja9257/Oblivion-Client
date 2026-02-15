package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import dev.oblivion.client.setting.impl.EnumSetting;
import net.minecraft.util.math.Vec3d;

public class ElytraFly extends Module {

    public enum Mode { BOOST, CONTROL }

    private final DoubleSetting speed = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Speed")
            .description("Elytra flight speed.")
            .defaultValue(1.5)
            .min(0.5)
            .max(5.0)
            .build()
    );

    private final EnumSetting<Mode> mode = settings.getDefaultGroup().add(
        new EnumSetting.Builder<Mode>()
            .name("Mode")
            .description("Elytra flight mode.")
            .defaultValue(Mode.BOOST)
            .build()
    );

    public ElytraFly() {
        super("ElytraFly", "Enhances elytra flight capabilities.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;
        if (!mc.player.isGliding()) return;

        double spd = speed.get();

        switch (mode.get()) {
            case BOOST -> {
                // Boost mode: add velocity in the direction the player is looking
                Vec3d look = mc.player.getRotationVector().normalize().multiply(spd * 0.05);
                mc.player.addVelocity(look.x, look.y, look.z);
            }
            case CONTROL -> {
                // Control mode: full directional control while elytra flying
                Vec3d forward = Vec3d.fromPolar(mc.player.getPitch(), mc.player.getYaw()).normalize();
                Vec3d velocity = Vec3d.ZERO;

                double moveSpeed = spd * 0.5;

                if (mc.options.forwardKey.isPressed()) {
                    velocity = velocity.add(forward.multiply(moveSpeed));
                }
                if (mc.options.backKey.isPressed()) {
                    velocity = velocity.add(forward.multiply(-moveSpeed));
                }
                if (mc.options.leftKey.isPressed()) {
                    Vec3d left = forward.rotateY((float) Math.toRadians(90));
                    velocity = velocity.add(new Vec3d(left.x, 0, left.z).normalize().multiply(moveSpeed));
                }
                if (mc.options.rightKey.isPressed()) {
                    Vec3d right = forward.rotateY((float) Math.toRadians(-90));
                    velocity = velocity.add(new Vec3d(right.x, 0, right.z).normalize().multiply(moveSpeed));
                }
                if (mc.options.jumpKey.isPressed()) {
                    velocity = velocity.add(0, moveSpeed, 0);
                }
                if (mc.options.sneakKey.isPressed()) {
                    velocity = velocity.add(0, -moveSpeed, 0);
                }

                mc.player.setVelocity(velocity);
            }
        }
    }
}
