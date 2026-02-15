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

public final class WurstMobSpawnEsp extends Module {
    private final IntSetting radius = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Radius").description("Spawner scan radius").defaultValue(24).range(6, 96).build()
    );

    private final IntSetting scanInterval = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Scan Interval").description("Ticks between scans").defaultValue(20).range(5, 200).build()
    );

    private final List<BlockPos> spawners = new ArrayList<>();
    private int ticks;

    public WurstMobSpawnEsp() {
        super("MobSpawnEsp", "Highlights monster spawners.", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        spawners.clear();
        ticks = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        ticks++;
        if (ticks < scanInterval.get()) return;
        ticks = 0;

        spawners.clear();
        BlockPos origin = mc.player.getBlockPos();
        int r = radius.get();

        for (int x = -r; x <= r; x++) {
            for (int y = -r / 2; y <= r / 2; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    if (mc.world.getBlockState(pos).getBlock() == Blocks.SPAWNER) {
                        spawners.add(pos.toImmutable());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRender(RenderEvent.World event) {
        if (mc.player == null || mc.world == null || spawners.isEmpty()) return;

        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        RenderUtil.setupRenderState(2.2f);
        for (BlockPos pos : spawners) {
            event.getMatrices().push();
            event.getMatrices().translate(pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z);
            RenderUtil.drawBoxOutline(event.getMatrices(), 0f, 0f, 0f, 1f, 1f, 1f, 1f, 0.1f, 0.1f, 0.95f);
            event.getMatrices().pop();
        }
        RenderUtil.teardownRenderState();
    }
}
