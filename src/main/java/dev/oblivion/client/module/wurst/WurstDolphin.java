package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.util.math.Vec3d;

public final class WurstDolphin extends Module {
    private final DoubleSetting speed = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Speed").description("Horizontal swim speed").defaultValue(0.8).range(0.2, 2.0).build()
    );

    public WurstDolphin() {
        super("Dolphin", "Boosts swimming speed in water.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        if (!mc.player.isTouchingWater()) return;

        Vec3d look = mc.player.getRotationVec(1.0f).normalize();
        Vec3d vel = mc.player.getVelocity();
        mc.player.setVelocity(look.x * speed.get(), vel.y + 0.02, look.z * speed.get());
    }
}
