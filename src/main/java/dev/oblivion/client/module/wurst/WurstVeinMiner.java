package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public final class WurstVeinMiner extends Module {
    private final IntSetting maxBlocks = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Max Blocks").description("Maximum blocks per vein").defaultValue(64).range(4, 256).build()
    );

    public WurstVeinMiner() {
        super("VeinMiner", "Mines connected ore veins.", Category.WORLD);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (!(mc.crosshairTarget instanceof net.minecraft.util.hit.BlockHitResult hit)) return;

        BlockPos start = hit.getBlockPos();
        if (mc.world.isAir(start)) return;

        Block target = mc.world.getBlockState(start).getBlock();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(start);

        int mined = 0;
        while (!queue.isEmpty() && mined < maxBlocks.get()) {
            BlockPos pos = queue.poll();
            if (!visited.add(pos)) continue;
            if (mc.world.getBlockState(pos).getBlock() != target) continue;

            mc.interactionManager.attackBlock(pos, hit.getSide());
            mined++;

            for (var dir : net.minecraft.util.math.Direction.values()) {
                BlockPos next = pos.offset(dir);
                if (!visited.contains(next)) queue.add(next);
            }
        }
    }
}
