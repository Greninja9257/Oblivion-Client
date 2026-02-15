package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.EnumSetting;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.util.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public final class WurstSearch extends Module {
    public enum Target {
        DIAMOND_ORE(Blocks.DIAMOND_ORE),
        ANCIENT_DEBRIS(Blocks.ANCIENT_DEBRIS),
        EMERALD_ORE(Blocks.EMERALD_ORE),
        SPAWNER(Blocks.SPAWNER);

        public final Block block;

        Target(Block block) {
            this.block = block;
        }
    }

    private final EnumSetting<Target> target = settings.getDefaultGroup().add(
        new EnumSetting.Builder<Target>().name("Target").description("Block to highlight").defaultValue(Target.DIAMOND_ORE).build()
    );

    private final IntSetting radius = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Radius").description("Search radius").defaultValue(24).range(6, 96).build()
    );

    private final List<BlockPos> found = new ArrayList<>();
    private int ticks;

    public WurstSearch() {
        super("Search", "Searches and highlights selected blocks.", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        found.clear();
        ticks = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        ticks++;
        if (ticks < 20) return;
        ticks = 0;

        found.clear();
        BlockPos origin = mc.player.getBlockPos();
        int r = radius.get();

        for (int x = -r; x <= r; x++) {
            for (int y = -r / 2; y <= r / 2; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    if (mc.world.getBlockState(pos).getBlock() == target.get().block) {
                        found.add(pos.toImmutable());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRender(RenderEvent.World event) {
        if (mc.player == null || mc.world == null || found.isEmpty()) return;

        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        RenderUtil.setupRenderState(1.7f);
        for (BlockPos pos : found) {
            event.getMatrices().push();
            event.getMatrices().translate(pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z);
            RenderUtil.drawBoxOutline(event.getMatrices(), 0f, 0f, 0f, 1f, 1f, 1f, 0.2f, 1f, 0.9f, 0.95f);
            event.getMatrices().pop();
        }
        RenderUtil.teardownRenderState();
    }
}
