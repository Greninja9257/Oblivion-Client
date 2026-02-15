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

public final class WurstBarrierEsp extends Module {
    private final IntSetting radius = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Radius").description("Barrier scan radius").defaultValue(16).range(4, 64).build()
    );

    public WurstBarrierEsp() {
        super("BarrierEsp", "Highlights nearby barrier blocks.", Category.RENDER);
    }

    @EventHandler
    public void onRender(RenderEvent.World event) {
        if (mc.player == null || mc.world == null) return;
        BlockPos origin = mc.player.getBlockPos();
        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        int r = radius.get();

        RenderUtil.setupRenderState(1.5f);
        for (int x = -r; x <= r; x++) {
            for (int y = -r / 2; y <= r / 2; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    if (!mc.world.getBlockState(pos).isOf(Blocks.BARRIER)) continue;
                    event.getMatrices().push();
                    event.getMatrices().translate(pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z);
                    RenderUtil.drawBoxOutline(event.getMatrices(), 0, 0, 0, 1, 1, 1, 1f, 0.2f, 0.2f, 0.95f);
                    event.getMatrices().pop();
                }
            }
        }
        RenderUtil.teardownRenderState();
    }
}
