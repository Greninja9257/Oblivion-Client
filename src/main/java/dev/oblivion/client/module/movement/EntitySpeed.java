package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EntitySpeed extends Module {

    private final DoubleSetting speed = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Speed")
            .description("Entity speed multiplier.")
            .defaultValue(1.5)
            .range(0.1, 10.0)
            .build()
    );

    public EntitySpeed() {
        super("EntitySpeed", "Controls the speed of the entity you are riding.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || !mc.player.hasVehicle()) return;

        Entity vehicle = mc.player.getVehicle();
        if (vehicle == null) return;

        float yaw = mc.player.getYaw();
        double forward = mc.player.input.movementForward;
        double sideways = mc.player.input.movementSideways;

        if (forward == 0 && sideways == 0) return;

        double angle = Math.toRadians(yaw);
        if (forward != 0) {
            if (sideways > 0) angle -= (forward > 0 ? 45 : -45);
            else if (sideways < 0) angle += (forward > 0 ? 45 : -45);
            if (forward > 0) angle -= Math.PI;
        } else {
            if (sideways > 0) angle -= Math.PI / 2;
            else if (sideways < 0) angle += Math.PI / 2;
        }

        double spd = 0.3 * speed.get();
        Vec3d vel = vehicle.getVelocity();
        vehicle.setVelocity(-Math.sin(angle) * spd, vel.y, Math.cos(angle) * spd);
    }
}
