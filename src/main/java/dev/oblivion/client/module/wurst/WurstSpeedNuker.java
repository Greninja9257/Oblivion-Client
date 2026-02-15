package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.util.math.BlockPos;

public final class WurstSpeedNuker extends Module {
    private final IntSetting blocksPerTick = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Blocks/Tick").description("Max block breaks per tick").defaultValue(8).range(1, 32).build()
    );

    private final IntSetting radius = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Radius").description("Break radius").defaultValue(4).range(1, 8).build()
    );

    public WurstSpeedNuker() {
        super("SpeedNuker", "Breaks many nearby blocks quickly.", Category.WORLD);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        int broken = 0;
        int r = radius.get();
        BlockPos origin = mc.player.getBlockPos();

        outer:
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    if (mc.world.isAir(pos)) continue;
                    mc.interactionManager.attackBlock(pos, net.minecraft.util.math.Direction.UP);
                    broken++;
                    if (broken >= blocksPerTick.get()) break outer;
                }
            }
        }
    }
}
