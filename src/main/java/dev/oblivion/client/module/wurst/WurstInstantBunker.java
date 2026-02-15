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

public final class WurstInstantBunker extends Module {
    public WurstInstantBunker() {
        super("InstantBunker", "Builds a quick protective shell around player.", Category.WORLD);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (!(mc.player.getMainHandStack().getItem() instanceof BlockItem)) return;

        BlockPos base = mc.player.getBlockPos();
        BlockPos[] targets = new BlockPos[]{
            base.north(), base.south(), base.east(), base.west(), base.up().north(), base.up().south(), base.up().east(), base.up().west(), base.up(2)
        };

        for (BlockPos pos : targets) {
            if (!mc.world.getBlockState(pos).isReplaceable()) continue;
            BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false);
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
        }
    }
}
