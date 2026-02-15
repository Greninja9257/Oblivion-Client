package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Vec3d;

public class BoatFly extends Module {

    private final DoubleSetting speed = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Speed")
            .description("Horizontal flight speed while in a boat.")
            .defaultValue(2.0)
            .min(0.5)
            .max(5.0)
            .build()
    );

    private final DoubleSetting upSpeed = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Up Speed")
            .description("Vertical ascent speed while in a boat.")
            .defaultValue(0.5)
            .min(0.1)
            .max(2.0)
            .build()
    );

    public BoatFly() {
        super("BoatFly", "Fly while riding a boat.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;
        if (!(mc.player.getVehicle() instanceof BoatEntity boat)) return;

        double spd = speed.get() / 5.0;
        Vec3d forward = Vec3d.fromPolar(0, mc.player.getYaw()).multiply(spd);
        Vec3d velocity = Vec3d.ZERO;

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
            velocity = velocity.add(0, upSpeed.get(), 0);
        }
        if (mc.options.sneakKey.isPressed()) {
            velocity = velocity.add(0, -upSpeed.get(), 0);
        }

        boat.setVelocity(velocity);
    }
}
