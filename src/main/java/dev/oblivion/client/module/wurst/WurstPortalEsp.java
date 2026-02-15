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

public final class WurstPortalEsp extends Module {
    private final IntSetting radius = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Radius").description("Search radius").defaultValue(20).range(4, 64).build()
    );

    public WurstPortalEsp() {
        super("PortalEsp", "Highlights nether portal blocks.", Category.RENDER);
    }

    @EventHandler
    public void onRender(RenderEvent.World event) {
        if (mc.player == null || mc.world == null) return;

        BlockPos origin = mc.player.getBlockPos();
        int r = radius.get();
        Vec3d cam = mc.gameRenderer.getCamera().getPos();

        RenderUtil.setupRenderState(1.8f);
        for (int x = -r; x <= r; x++) {
            for (int y = -r / 2; y <= r / 2; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    if (mc.world.getBlockState(pos).getBlock() != Blocks.NETHER_PORTAL) continue;

                    event.getMatrices().push();
                    event.getMatrices().translate(pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z);
                    RenderUtil.drawBoxOutline(event.getMatrices(), 0f, 0f, 0f, 1f, 1f, 1f, 0.7f, 0.2f, 1f, 0.9f);
                    event.getMatrices().pop();
                }
            }
        }
        RenderUtil.teardownRenderState();
    }
}
