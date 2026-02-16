package dev.oblivion.client.module.render;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.setting.impl.BoolSetting;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class HoleESP extends Module {

    public enum HoleType { BEDROCK, OBSIDIAN, MIXED }

    private final IntSetting range = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Range")
            .description("Search range for holes.")
            .defaultValue(8)
            .range(1, 32)
            .build()
    );

    private final BoolSetting showBedrock = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Bedrock Holes")
            .description("Show bedrock holes (safest).")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting showObsidian = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Obsidian Holes")
            .description("Show obsidian holes.")
            .defaultValue(true)
            .build()
    );

    private final List<BlockPos> bedrockHoles = new ArrayList<>();
    private final List<BlockPos> obsidianHoles = new ArrayList<>();
    private int tick = 0;

    public HoleESP() {
        super("HoleESP", "Highlights safe holes for crystal PvP.", Category.RENDER);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        tick++;
        if (tick % 5 != 0) return;

        bedrockHoles.clear();
        obsidianHoles.clear();

        BlockPos center = mc.player.getBlockPos();
        int r = range.get();

        for (BlockPos pos : BlockPos.iterateOutwards(center, r, r, r)) {
            if (!mc.world.getBlockState(pos).isAir()) continue;
            if (!mc.world.getBlockState(pos.up()).isAir()) continue;

            boolean isBedrock = true;
            boolean isObsidian = true;
            boolean valid = true;

            BlockPos[] surrounding = {pos.down(), pos.north(), pos.south(), pos.east(), pos.west()};
            for (BlockPos check : surrounding) {
                BlockState state = mc.world.getBlockState(check);
                if (state.getBlock() != Blocks.BEDROCK && state.getBlock() != Blocks.OBSIDIAN) {
                    valid = false;
                    break;
                }
                if (state.getBlock() != Blocks.BEDROCK) isBedrock = false;
                if (state.getBlock() != Blocks.OBSIDIAN) isObsidian = false;
            }

            if (!valid) continue;

            if (isBedrock && showBedrock.get()) bedrockHoles.add(pos.toImmutable());
            else if (showObsidian.get()) obsidianHoles.add(pos.toImmutable());
        }
    }

    public List<BlockPos> getBedrockHoles() { return bedrockHoles; }
    public List<BlockPos> getObsidianHoles() { return obsidianHoles; }
}
