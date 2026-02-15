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

public final class WurstScaffoldWalk extends Module {
    public WurstScaffoldWalk() {
        super("ScaffoldWalk", "Places blocks under you while walking.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (!(mc.player.getMainHandStack().getItem() instanceof BlockItem)) return;

        if (mc.player.input.movementForward == 0 && mc.player.input.movementSideways == 0) return;

        BlockPos below = mc.player.getBlockPos().down();
        if (!mc.world.getBlockState(below).isReplaceable()) return;

        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(below), Direction.UP, below, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
    }
}
