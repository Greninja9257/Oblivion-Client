package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.util.RenderUtil;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class WurstOpenWaterEsp extends Module {
    private final IntSetting radius = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Radius").description("Scan radius").defaultValue(18).range(4, 64).build()
    );

    public WurstOpenWaterEsp() {
        super("OpenWaterEsp", "Highlights open-water fishing spots.", Category.RENDER);
    }

    @EventHandler
    public void onRender(RenderEvent.World event) {
        if (mc.player == null || mc.world == null) return;
        BlockPos o = mc.player.getBlockPos();
        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        int r = radius.get();

        RenderUtil.setupRenderState(1.3f);
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                BlockPos p = o.add(x, 0, z);
                if (!mc.world.getBlockState(p).isOf(Blocks.WATER)) continue;
                if (!mc.world.getBlockState(p.up()).isAir()) continue;

                event.getMatrices().push();
                event.getMatrices().translate(p.getX() - cam.x, p.getY() - cam.y, p.getZ() - cam.z);
                RenderUtil.drawBoxOutline(event.getMatrices(), 0, 0, 0, 1, 1, 1, 0.1f, 0.6f, 1f, 0.65f);
                event.getMatrices().pop();
            }
        }
        RenderUtil.teardownRenderState();
    }
}
