package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class WurstBonemealAura extends Module {
    public WurstBonemealAura() {
        super("BonemealAura", "Uses bonemeal on nearby crops.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        int slot = findBonemeal();
        if (slot == -1) return;

        BlockPos origin = mc.player.getBlockPos();
        for (int x = -3; x <= 3; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    if (!(mc.world.getBlockState(pos).getBlock() instanceof net.minecraft.block.Fertilizable)) continue;

                    int prev = mc.player.getInventory().selectedSlot;
                    mc.player.getInventory().selectedSlot = slot;
                    BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
                    mc.player.getInventory().selectedSlot = prev;
                    return;
                }
            }
        }
    }

    private int findBonemeal() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.BONE_MEAL) return i;
        }
        return -1;
    }
}
