package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Scaffold extends Module {

    private final BoolSetting tower = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Tower")
            .description("Automatically tower up when holding jump.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting rotate = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Rotate")
            .description("Rotate towards the block placement position.")
            .defaultValue(true)
            .build()
    );

    public Scaffold() {
        super("Scaffold", "Automatically places blocks under your feet.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        BlockPos below = mc.player.getBlockPos().down();

        if (!mc.world.getBlockState(below).isReplaceable()) return;

        // Find a block item in hotbar
        int blockSlot = findBlockSlot();
        if (blockSlot == -1) return;

        int previousSlot = mc.player.getInventory().selectedSlot;
        mc.player.getInventory().selectedSlot = blockSlot;

        // Find a face to place against
        PlacementInfo placement = findPlacement(below);
        if (placement != null) {
            if (rotate.get()) {
                float[] rotations = calculateRotations(placement.hitPos());
                mc.player.setYaw(rotations[0]);
                mc.player.setPitch(rotations[1]);
            }

            BlockHitResult hitResult = new BlockHitResult(
                placement.hitPos(), placement.direction(), placement.neighbor(), false
            );

            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
            mc.player.swingHand(Hand.MAIN_HAND);

            // Tower: move player upward when jumping
            if (tower.get() && mc.options.jumpKey.isPressed()) {
                mc.player.setVelocity(mc.player.getVelocity().x, 0.42, mc.player.getVelocity().z);
            }
        }

        mc.player.getInventory().selectedSlot = previousSlot;
    }

    private int findBlockSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                return i;
            }
        }
        return -1;
    }

    private PlacementInfo findPlacement(BlockPos target) {
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = target.offset(dir);
            BlockState state = mc.world.getBlockState(neighbor);
            if (!state.isReplaceable() && !state.isAir()) {
                Vec3d hitPos = Vec3d.ofCenter(neighbor).add(
                    Vec3d.of(dir.getOpposite().getVector()).multiply(0.5)
                );
                return new PlacementInfo(neighbor, dir.getOpposite(), hitPos);
            }
        }
        return null;
    }

    private float[] calculateRotations(Vec3d target) {
        Vec3d eyes = mc.player.getEyePos();
        double dx = target.x - eyes.x;
        double dy = target.y - eyes.y;
        double dz = target.z - eyes.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(-Math.atan2(dx, dz));
        float pitch = (float) Math.toDegrees(-Math.atan2(dy, dist));
        return new float[]{ yaw, pitch };
    }

    private record PlacementInfo(BlockPos neighbor, Direction direction, Vec3d hitPos) {}
}
