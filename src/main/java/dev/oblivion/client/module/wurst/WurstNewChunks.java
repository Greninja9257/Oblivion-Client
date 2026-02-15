package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.util.RenderUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Set;

public final class WurstNewChunks extends Module {
    private final Set<ChunkPos> seen = new HashSet<>();
    private final Set<ChunkPos> newlySeen = new HashSet<>();

    public WurstNewChunks() {
        super("NewChunks", "Highlights chunks first seen during this session.", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        seen.clear();
        newlySeen.clear();
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        ChunkPos cp = new ChunkPos(mc.player.getBlockPos());
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                ChunkPos p = new ChunkPos(cp.x + x, cp.z + z);
                if (seen.add(p)) {
                    newlySeen.add(p);
                }
            }
        }
    }

    @EventHandler
    public void onRender(RenderEvent.World event) {
        if (mc.player == null || mc.world == null || newlySeen.isEmpty()) return;
        Vec3d cam = mc.gameRenderer.getCamera().getPos();

        RenderUtil.setupRenderState(2f);
        for (ChunkPos chunk : newlySeen) {
            int bx = chunk.getStartX();
            int bz = chunk.getStartZ();
            int by = mc.world.getBottomY();
            int ty = by + 8;

            event.getMatrices().push();
            event.getMatrices().translate(bx - cam.x, by - cam.y, bz - cam.z);
            RenderUtil.drawBoxOutline(event.getMatrices(), 0, 0, 0, 16, ty - by, 16, 0.2f, 0.95f, 1f, 0.65f);
            event.getMatrices().pop();
        }
        RenderUtil.teardownRenderState();
    }
}
