package dev.oblivion.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PlayerUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static ClientPlayerEntity player() {
        return mc.player;
    }

    public static boolean isValid(Entity entity) {
        if (entity == null) return false;
        if (entity == mc.player) return false;
        if (!entity.isAlive()) return false;
        return true;
    }

    public static double distanceTo(Entity entity) {
        if (mc.player == null) return Double.MAX_VALUE;
        return mc.player.distanceTo(entity);
    }

    public static float[] getRotationsTo(Entity entity) {
        return getRotationsTo(entity.getPos().add(0, entity.getEyeHeight(entity.getPose()) / 2.0, 0));
    }

    public static float[] getRotationsTo(Vec3d target) {
        if (mc.player == null) return new float[]{0, 0};
        Vec3d eyePos = mc.player.getEyePos();
        double dx = target.x - eyePos.x;
        double dy = target.y - eyePos.y;
        double dz = target.z - eyePos.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
        return new float[]{
            MathHelper.wrapDegrees(yaw),
            MathHelper.clamp(pitch, -90f, 90f)
        };
    }

    public static boolean isPlayer(Entity entity) {
        return entity instanceof PlayerEntity;
    }

    public static boolean isMonster(Entity entity) {
        return entity instanceof Monster;
    }

    public static boolean isAnimal(Entity entity) {
        return entity instanceof AnimalEntity;
    }

    public static int findItemInHotbar(Item item) {
        if (mc.player == null) return -1;
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == item) return i;
        }
        return -1;
    }

    public static boolean isHoldingWeapon() {
        if (mc.player == null) return false;
        Item item = mc.player.getMainHandStack().getItem();
        return item instanceof SwordItem || item instanceof AxeItem;
    }
}
