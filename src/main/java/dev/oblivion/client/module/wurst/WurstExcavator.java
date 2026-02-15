package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public final class WurstExcavator extends Module {
    private final IntSetting radius = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Radius").description("Excavate radius around looked block").defaultValue(1).range(1, 4).build()
    );

    public WurstExcavator() {
        super("Excavator", "Mines a small area of matching blocks.", Category.WORLD);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (!(mc.crosshairTarget instanceof net.minecraft.util.hit.BlockHitResult hit)) return;

        BlockPos center = hit.getBlockPos();
        Block target = mc.world.getBlockState(center).getBlock();
        if (mc.world.isAir(center)) return;

        int r = radius.get();
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = center.add(x, y, z);
                    if (mc.world.getBlockState(pos).getBlock() != target) continue;
                    mc.interactionManager.attackBlock(pos, hit.getSide());
                }
            }
        }
    }
}
