package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public final class WurstTreeBot extends Module {
    private final IntSetting radius = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Radius").description("Log search radius").defaultValue(6).range(2, 16).build()
    );

    public WurstTreeBot() {
        super("TreeBot", "Automatically chops nearby logs.", Category.WORLD);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        BlockPos origin = mc.player.getBlockPos();
        int r = radius.get();

        for (int x = -r; x <= r; x++) {
            for (int y = -2; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    if (state.isOf(Blocks.OAK_LOG) || state.isOf(Blocks.BIRCH_LOG) || state.isOf(Blocks.SPRUCE_LOG)
                        || state.isOf(Blocks.JUNGLE_LOG) || state.isOf(Blocks.DARK_OAK_LOG) || state.isOf(Blocks.ACACIA_LOG)
                        || state.isOf(Blocks.CHERRY_LOG) || state.isOf(Blocks.MANGROVE_LOG)) {
                        mc.interactionManager.attackBlock(pos, net.minecraft.util.math.Direction.UP);
                        return;
                    }
                }
            }
        }
    }
}
