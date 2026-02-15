package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.util.math.BlockPos;

public final class WurstNukerLegit extends Module {
    private final IntSetting radius = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Radius").description("Break radius").defaultValue(4).range(1, 8).build()
    );

    private int index;

    public WurstNukerLegit() {
        super("NukerLegit", "Breaks one block per tick in a nearby radius.", Category.WORLD);
    }

    @Override
    protected void onEnable() {
        index = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        int r = radius.get();
        int diameter = r * 2 + 1;
        int total = diameter * diameter * diameter;
        index = (index + 1) % total;

        int x = index % diameter - r;
        int y = (index / diameter) % diameter - r;
        int z = (index / (diameter * diameter)) % diameter - r;

        BlockPos pos = mc.player.getBlockPos().add(x, y, z);
        if (!mc.world.isAir(pos)) {
            mc.interactionManager.attackBlock(pos, net.minecraft.util.math.Direction.UP);
        }
    }
}
