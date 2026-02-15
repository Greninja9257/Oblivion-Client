package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class WurstAntiCactus extends Module {
    public WurstAntiCactus() {
        super("AntiCactus", "Automatically breaks nearby cactus blocks.", Category.WORLD);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        BlockPos origin = mc.player.getBlockPos();
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    if (mc.world.getBlockState(pos).isOf(Blocks.CACTUS)) {
                        mc.interactionManager.attackBlock(pos, Direction.UP);
                        return;
                    }
                }
            }
        }
    }
}
