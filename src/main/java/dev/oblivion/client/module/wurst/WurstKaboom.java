package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class WurstKaboom extends Module {
    public WurstKaboom() {
        super("Kaboom", "Ignites nearby TNT blocks.", Category.WORLD);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        BlockPos origin = mc.player.getBlockPos();
        for (int x = -4; x <= 4; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -4; z <= 4; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    if (mc.world.getBlockState(pos).isOf(Blocks.TNT)) {
                        mc.interactionManager.attackBlock(pos, Direction.UP);
                        return;
                    }
                }
            }
        }
    }
}
