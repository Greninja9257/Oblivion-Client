package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class WurstBuildRandom extends Module {
    private final Random random = new Random();

    public WurstBuildRandom() {
        super("BuildRandom", "Places random hotbar blocks near player.", Category.WORLD);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        List<Integer> blockSlots = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() instanceof BlockItem) {
                blockSlots.add(i);
            }
        }
        if (blockSlots.isEmpty()) return;

        int slot = blockSlots.get(random.nextInt(blockSlots.size()));
        int prev = mc.player.getInventory().selectedSlot;
        mc.player.getInventory().selectedSlot = slot;

        BlockPos place = mc.player.getBlockPos().add(random.nextInt(5) - 2, -1, random.nextInt(5) - 2);
        if (mc.world.getBlockState(place).isReplaceable()) {
            BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(place), Direction.UP, place, false);
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
        }

        mc.player.getInventory().selectedSlot = prev;
    }
}
