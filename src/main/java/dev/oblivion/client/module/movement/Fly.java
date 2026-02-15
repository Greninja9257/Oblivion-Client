package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import dev.oblivion.client.setting.impl.EnumSetting;
import net.minecraft.util.math.Vec3d;

public class Fly extends Module {

    public enum Mode { VANILLA, CREATIVE }

    private final DoubleSetting speed = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Speed")
            .description("Flight speed multiplier.")
            .defaultValue(2.0)
            .min(0.5)
            .max(10.0)
            .build()
    );

    private final EnumSetting<Mode> mode = settings.getDefaultGroup().add(
        new EnumSetting.Builder<Mode>()
            .name("Mode")
            .description("Flight mode.")
            .defaultValue(Mode.VANILLA)
            .build()
    );

    public Fly() {
        super("Fly", "Allows you to fly in survival mode.", Category.MOVEMENT);
    }

    public void handleTravel(Vec3d movementInput) {
        // Travel is overridden through tick-based velocity control.
    }

    @Override
    protected void onDisable() {
        if (mc.player == null) return;
        mc.player.getAbilities().flying = false;
        mc.player.getAbilities().allowFlying = false;
        mc.player.setVelocity(Vec3d.ZERO);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        switch (mode.get()) {
            case CREATIVE -> {
                mc.player.getAbilities().allowFlying = true;
                mc.player.getAbilities().flying = true;
                mc.player.getAbilities().setFlySpeed((float) (speed.get() / 10.0));
            }
            case VANILLA -> {
                mc.player.getAbilities().flying = false;

                Vec3d velocity = Vec3d.ZERO;
                double spd = speed.get() / 5.0;

                Vec3d forward = Vec3d.fromPolar(0, mc.player.getYaw()).multiply(spd);

                if (mc.options.forwardKey.isPressed()) {
                    velocity = velocity.add(forward);
                }
                if (mc.options.backKey.isPressed()) {
                    velocity = velocity.add(forward.negate());
                }
                if (mc.options.leftKey.isPressed()) {
                    velocity = velocity.add(forward.rotateY((float) Math.toRadians(90)));
                }
                if (mc.options.rightKey.isPressed()) {
                    velocity = velocity.add(forward.rotateY((float) Math.toRadians(-90)));
                }
                if (mc.options.jumpKey.isPressed()) {
                    velocity = velocity.add(0, spd, 0);
                }
                if (mc.options.sneakKey.isPressed()) {
                    velocity = velocity.add(0, -spd, 0);
                }

                mc.player.setVelocity(velocity);
            }
        }
    }
}
