package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public final class WurstFollow extends Module {
    private final DoubleSetting followDistance = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Distance").description("Desired follow distance").defaultValue(3.0).range(1.0, 12.0).build()
    );

    private final DoubleSetting speed = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Speed").description("Follow movement speed").defaultValue(0.25).range(0.05, 1.0).build()
    );

    public WurstFollow() {
        super("Follow", "Follows the nearest player.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        PlayerEntity target = null;
        double best = Double.MAX_VALUE;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            double dist = mc.player.squaredDistanceTo(player);
            if (dist < best) {
                best = dist;
                target = player;
            }
        }

        if (target == null) return;

        Vec3d to = target.getPos().subtract(mc.player.getPos());
        double dist = to.length();
        if (dist <= followDistance.get()) {
            mc.player.setVelocity(mc.player.getVelocity().x * 0.4, mc.player.getVelocity().y, mc.player.getVelocity().z * 0.4);
            return;
        }

        Vec3d dir = to.normalize().multiply(speed.get());
        mc.player.setVelocity(dir.x, mc.player.getVelocity().y, dir.z);
    }
}
