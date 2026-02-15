package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.util.RenderUtil;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public final class WurstBaseFinder extends Module {
    private final IntSetting radius = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Radius").description("Scan radius").defaultValue(32).range(8, 96).build()
    );

    private final List<BlockPos> suspicious = new ArrayList<>();
    private int ticks;

    public WurstBaseFinder() {
        super("BaseFinder", "Marks suspicious chest/furnace clusters.", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        suspicious.clear();
        ticks = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;
        if (++ticks < 40) return;
        ticks = 0;
        suspicious.clear();

        BlockPos o = mc.player.getBlockPos();
        int r = radius.get();
        for (int x = -r; x <= r; x++) {
            for (int y = -r / 2; y <= r / 2; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos p = o.add(x, y, z);
                    var b = mc.world.getBlockState(p).getBlock();
                    if (b == Blocks.CHEST || b == Blocks.BARREL || b == Blocks.FURNACE || b == Blocks.BLAST_FURNACE) {
                        suspicious.add(p.toImmutable());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRender(RenderEvent.World event) {
        if (mc.player == null || mc.world == null || suspicious.isEmpty()) return;
        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        RenderUtil.setupRenderState(1.7f);
        for (BlockPos p : suspicious) {
            event.getMatrices().push();
            event.getMatrices().translate(p.getX() - cam.x, p.getY() - cam.y, p.getZ() - cam.z);
            RenderUtil.drawBoxOutline(event.getMatrices(), 0, 0, 0, 1, 1, 1, 0.9f, 0.9f, 0.2f, 0.9f);
            event.getMatrices().pop();
        }
        RenderUtil.teardownRenderState();
    }
}
