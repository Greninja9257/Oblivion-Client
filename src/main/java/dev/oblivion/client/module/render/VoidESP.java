package dev.oblivion.client.module.render;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class VoidESP extends Module {

    private final IntSetting range = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Range")
            .description("Horizontal search range.")
            .defaultValue(8)
            .range(1, 32)
            .build()
    );

    private final List<BlockPos> voidHoles = new ArrayList<>();
    private int tick = 0;

    public VoidESP() {
        super("VoidESP", "Highlights holes leading to the void.", Category.RENDER);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        tick++;
        if (tick % 10 != 0) return;

        voidHoles.clear();

        BlockPos center = mc.player.getBlockPos();
        int r = range.get();
        int minY = mc.world.getBottomY();

        for (int x = center.getX() - r; x <= center.getX() + r; x++) {
            for (int z = center.getZ() - r; z <= center.getZ() + r; z++) {
                BlockPos pos = new BlockPos(x, minY, z);
                if (mc.world.getBlockState(pos).isAir()) {
                    voidHoles.add(pos);
                }
            }
        }
    }

    public List<BlockPos> getVoidHoles() { return voidHoles; }

    @Override
    protected void onDisable() {
        voidHoles.clear();
    }
}
