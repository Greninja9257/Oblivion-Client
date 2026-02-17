package dev.oblivion.client.bot;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.function.Predicate;

public final class BotBlockSearch {
    private BotBlockSearch() {}

    public static BlockPos findNearest(double fromX, double fromY, double fromZ, int radius, Predicate<BlockState> predicate) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return null;

        int bx = (int) Math.floor(fromX);
        int by = (int) Math.floor(fromY);
        int bz = (int) Math.floor(fromZ);

        BlockPos best = null;
        double bestSq = Double.MAX_VALUE;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = new BlockPos(bx + dx, by + dy, bz + dz);
                    if (!mc.world.isChunkLoaded(pos)) continue;

                    BlockState state = mc.world.getBlockState(pos);
                    if (!predicate.test(state)) continue;

                    double distSq = pos.getSquaredDistance(fromX, fromY, fromZ);
                    if (distSq < bestSq) {
                        bestSq = distSq;
                        best = pos;
                    }
                }
            }
        }

        return best;
    }

    public static BlockState getBlockState(BlockPos pos) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || pos == null || !mc.world.isChunkLoaded(pos)) return null;
        return mc.world.getBlockState(pos);
    }

    public static boolean isBlock(BlockPos pos, Block block) {
        BlockState state = getBlockState(pos);
        return state != null && state.isOf(block);
    }
}
