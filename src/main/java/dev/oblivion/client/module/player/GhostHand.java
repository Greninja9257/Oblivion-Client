package dev.oblivion.client.module.player;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class GhostHand extends Module {

    public GhostHand() {
        super("GhostHand", "Allows you to interact with containers through blocks.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (!mc.options.useKey.isPressed()) return;

        Vec3d eyePos = mc.player.getEyePos();
        Vec3d lookVec = mc.player.getRotationVec(1.0f);
        double reach = 5.0;

        for (double d = 0.5; d <= reach; d += 0.5) {
            Vec3d pos = eyePos.add(lookVec.multiply(d));
            BlockPos blockPos = BlockPos.ofFloored(pos);
            BlockState state = mc.world.getBlockState(blockPos);

            if (state.getBlock() instanceof ChestBlock
                || state.getBlock() instanceof EnderChestBlock
                || state.getBlock() instanceof ShulkerBoxBlock) {
                BlockHitResult hit = new BlockHitResult(pos, Direction.UP, blockPos, false);
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
                return;
            }
        }
    }
}
