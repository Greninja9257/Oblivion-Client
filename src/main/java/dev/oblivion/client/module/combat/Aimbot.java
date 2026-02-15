package dev.oblivion.client.module.combat;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.DoubleSetting;
import dev.oblivion.client.setting.impl.EnumSetting;
import dev.oblivion.client.util.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Aimbot extends Module {

    public enum AimTarget { HEAD, BODY, FEET }

    private final DoubleSetting range = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Range")
            .description("Maximum aim range in blocks.")
            .defaultValue(4.5)
            .range(1.0, 6.0)
            .build()
    );

    private final DoubleSetting speed = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Speed")
            .description("Aim rotation speed.")
            .defaultValue(5.0)
            .range(1.0, 10.0)
            .build()
    );

    private final BoolSetting targetPlayers = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Target Players")
            .description("Aim at players.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting targetMobs = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Target Mobs")
            .description("Aim at hostile mobs.")
            .defaultValue(true)
            .build()
    );

    private final EnumSetting<AimTarget> aimAt = settings.getDefaultGroup().add(
        new EnumSetting.Builder<AimTarget>()
            .name("Aim At")
            .description("Which part of the entity to aim at.")
            .defaultValue(AimTarget.HEAD)
            .build()
    );

    public Aimbot() {
        super("Aimbot", "Smoothly aims at the nearest valid target.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        LivingEntity target = findTarget();
        if (target == null) return;

        Vec3d aimPoint = getAimPoint(target);
        float[] targetRots = PlayerUtil.getRotationsTo(aimPoint);

        float currentYaw = mc.player.getYaw();
        float currentPitch = mc.player.getPitch();

        float yawDiff = MathHelper.wrapDegrees(targetRots[0] - currentYaw);
        float pitchDiff = MathHelper.wrapDegrees(targetRots[1] - currentPitch);

        float factor = (float) (speed.get() / 10.0);

        float newYaw = currentYaw + yawDiff * factor;
        float newPitch = MathHelper.clamp(currentPitch + pitchDiff * factor, -90f, 90f);

        mc.player.setYaw(newYaw);
        mc.player.setPitch(newPitch);
    }

    private Vec3d getAimPoint(LivingEntity entity) {
        return switch (aimAt.get()) {
            case HEAD -> entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0);
            case BODY -> entity.getPos().add(0, entity.getHeight() / 2.0, 0);
            case FEET -> entity.getPos();
        };
    }

    private LivingEntity findTarget() {
        LivingEntity closest = null;
        double closestDist = range.get();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (!PlayerUtil.isValid(entity)) continue;

            double dist = PlayerUtil.distanceTo(entity);
            if (dist > closestDist) continue;

            if (entity instanceof PlayerEntity && !targetPlayers.get()) continue;
            if (entity instanceof Monster && !targetMobs.get()) continue;
            if (entity instanceof AnimalEntity) continue; // No animal targeting setting

            if (!(entity instanceof PlayerEntity) && !(entity instanceof Monster)) continue;

            closestDist = dist;
            closest = living;
        }

        return closest;
    }
}
