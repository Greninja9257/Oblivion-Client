package dev.oblivion.client.module.world;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.util.ChatUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StashFinder extends Module {

    private final IntSetting threshold = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Threshold")
            .description("Minimum container count in a chunk to consider it a stash.")
            .defaultValue(5)
            .range(1, 50)
            .build()
    );

    private final Map<ChunkPos, Integer> chunkContainerCounts = new HashMap<>();
    private final Set<ChunkPos> reportedChunks = new HashSet<>();

    public StashFinder() {
        super("StashFinder", "Detects chunks with many storage containers (potential stashes).", Category.WORLD);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        // Scan nearby loaded chunks
        ChunkPos playerChunk = mc.player.getChunkPos();
        int viewDist = mc.options.getViewDistance().getValue();

        for (int cx = playerChunk.x - viewDist; cx <= playerChunk.x + viewDist; cx++) {
            for (int cz = playerChunk.z - viewDist; cz <= playerChunk.z + viewDist; cz++) {
                ChunkPos cp = new ChunkPos(cx, cz);
                if (reportedChunks.contains(cp)) continue;
                if (!mc.world.isChunkLoaded(cx, cz)) continue;

                int count = 0;
                int topY = mc.world.getBottomY() + mc.world.getHeight();
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = mc.world.getBottomY(); y < topY; y++) {
                            BlockPos pos = new BlockPos(cx * 16 + x, y, cz * 16 + z);
                            BlockState state = mc.world.getBlockState(pos);
                            if (state.getBlock() instanceof ChestBlock || state.getBlock() instanceof ShulkerBoxBlock) {
                                count++;
                            }
                        }
                    }
                }

                chunkContainerCounts.put(cp, count);
                if (count >= threshold.get()) {
                    reportedChunks.add(cp);
                    ChatUtil.info(String.format("Potential stash at chunk [%d, %d] with %d containers", cx, cz, count));
                }
            }
        }
    }

    @Override
    protected void onDisable() {
        chunkContainerCounts.clear();
        reportedChunks.clear();
    }
}
