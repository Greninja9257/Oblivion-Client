package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.block.CropBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class WurstAutoFarm extends Module {
    private final IntSetting radius = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Radius").description("Farm radius").defaultValue(4).range(1, 12).build()
    );

    public WurstAutoFarm() {
        super("AutoFarm", "Harvests mature crops around you.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        BlockPos origin = mc.player.getBlockPos();
        int r = radius.get();

        for (int x = -r; x <= r; x++) {
            for (int y = -1; y <= 2; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    var state = mc.world.getBlockState(pos);
                    if (state.getBlock() instanceof CropBlock crop && crop.isMature(state)) {
                        mc.interactionManager.attackBlock(pos, Direction.UP);
                        return;
                    }
                }
            }
        }
    }
}
