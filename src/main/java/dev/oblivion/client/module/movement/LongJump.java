package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.util.math.Vec3d;

public class LongJump extends Module {

    private final DoubleSetting boost = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Boost")
            .description("Horizontal boost amount.")
            .defaultValue(1.5)
            .range(0.5, 5.0)
            .build()
    );

    private boolean wasOnGround = true;

    public LongJump() {
        super("LongJump", "Jump further horizontally.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null) return;

        if (!mc.player.isOnGround() && wasOnGround && mc.player.getVelocity().y > 0) {
            float yaw = mc.player.getYaw();
            double rad = Math.toRadians(yaw);
            Vec3d vel = mc.player.getVelocity();
            double speed = boost.get() * 0.3;
            mc.player.setVelocity(vel.x - Math.sin(rad) * speed, vel.y, vel.z + Math.cos(rad) * speed);
        }
        wasOnGround = mc.player.isOnGround();
    }
}
