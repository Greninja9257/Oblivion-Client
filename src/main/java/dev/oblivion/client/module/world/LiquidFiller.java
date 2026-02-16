package dev.oblivion.client.module.world;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.setting.impl.BoolSetting;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class LiquidFiller extends Module {

    private final IntSetting range = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Range")
            .description("Range to fill liquids.")
            .defaultValue(4)
            .range(1, 6)
            .build()
    );

    private final BoolSetting water = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Water")
            .description("Fill water sources.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting lava = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Lava")
            .description("Fill lava sources.")
            .defaultValue(true)
            .build()
    );

    public LiquidFiller() {
        super("LiquidFiller", "Fills in nearby liquid source blocks.", Category.WORLD);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        int blockSlot = findBlockSlot();
        if (blockSlot == -1) return;

        BlockPos playerPos = mc.player.getBlockPos();
        int r = range.get();

        for (BlockPos pos : BlockPos.iterateOutwards(playerPos, r, r, r)) {
            BlockState state = mc.world.getBlockState(pos);
            if (!(state.getBlock() instanceof FluidBlock)) continue;

            if (state.getBlock() == Blocks.WATER && !water.get()) continue;
            if (state.getBlock() == Blocks.LAVA && !lava.get()) continue;

            // Only fill source blocks (level 0)
            if (state.get(FluidBlock.LEVEL) != 0) continue;

            int prev = mc.player.getInventory().selectedSlot;
            mc.player.getInventory().selectedSlot = blockSlot;

            BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false);
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);

            mc.player.getInventory().selectedSlot = prev;
            return; // One per tick
        }
    }

    private int findBlockSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) return i;
        }
        return -1;
    }
}
