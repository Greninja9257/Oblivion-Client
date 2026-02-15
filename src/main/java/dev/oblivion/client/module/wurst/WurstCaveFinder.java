package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.util.RenderUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public final class WurstCaveFinder extends Module {
    private final IntSetting radius = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Radius").description("Scan radius").defaultValue(16).range(6, 48).build()
    );

    private final List<BlockPos> caveAir = new ArrayList<>();
    private int ticks;

    public WurstCaveFinder() {
        super("CaveFinder", "Finds cave air pockets underground.", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        caveAir.clear();
        ticks = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        ticks++;
        if (ticks < 15) return;
        ticks = 0;

        caveAir.clear();
        BlockPos origin = mc.player.getBlockPos();
        int r = radius.get();

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= 2; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    if (pos.getY() > mc.player.getY() - 2) continue;
                    if (!mc.world.getBlockState(pos).isAir()) continue;

                    int solidNeighbors = 0;
                    for (var dir : net.minecraft.util.math.Direction.values()) {
                        if (!mc.world.getBlockState(pos.offset(dir)).isAir()) solidNeighbors++;
                    }

                    if (solidNeighbors >= 4) {
                        caveAir.add(pos.toImmutable());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRender(RenderEvent.World event) {
        if (mc.player == null || mc.world == null || caveAir.isEmpty()) return;

        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        RenderUtil.setupRenderState(1.2f);
        for (BlockPos pos : caveAir) {
            event.getMatrices().push();
            event.getMatrices().translate(pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z);
            RenderUtil.drawBoxOutline(event.getMatrices(), 0f, 0f, 0f, 1f, 1f, 1f, 0.3f, 0.7f, 1f, 0.6f);
            event.getMatrices().pop();
        }
        RenderUtil.teardownRenderState();
    }
}
