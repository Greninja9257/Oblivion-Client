package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public final class WurstAimAssist extends Module {
    private final DoubleSetting range = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Range").description("Target range").defaultValue(6.0).range(1.0, 12.0).build()
    );

    private final IntSetting fov = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("FOV").description("Max aim-assist angle").defaultValue(90).range(10, 180).build()
    );

    private final DoubleSetting smoothness = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Smoothness").description("Rotation smoothing factor").defaultValue(0.25).range(0.05, 1.0).build()
    );

    public WurstAimAssist() {
        super("AimAssist", "Smoothly assists aim toward nearby targets.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        LivingEntity target = findTarget();
        if (target == null) return;

        double dx = target.getX() - mc.player.getX();
        double dz = target.getZ() - mc.player.getZ();
        double dy = target.getEyeY() - mc.player.getEyeY();
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));

        float yawDiff = wrapDegrees(targetYaw - mc.player.getYaw());
        float pitchDiff = wrapDegrees(targetPitch - mc.player.getPitch());

        if (Math.abs(yawDiff) > fov.get() / 2f) return;

        float factor = smoothness.get().floatValue();
        mc.player.setYaw(mc.player.getYaw() + yawDiff * factor);
        mc.player.setPitch(mc.player.getPitch() + pitchDiff * factor);
    }

    private LivingEntity findTarget() {
        LivingEntity best = null;
        double bestDistSq = range.get() * range.get();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity living) || entity == mc.player || !entity.isAlive()) continue;

            double distSq = mc.player.squaredDistanceTo(entity);
            if (distSq > bestDistSq) continue;

            bestDistSq = distSq;
            best = living;
        }

        return best;
    }

    private float wrapDegrees(float value) {
        float wrapped = value % 360f;
        if (wrapped >= 180f) wrapped -= 360f;
        if (wrapped < -180f) wrapped += 360f;
        return wrapped;
    }
}
