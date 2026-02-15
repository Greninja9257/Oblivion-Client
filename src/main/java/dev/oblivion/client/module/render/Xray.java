package dev.oblivion.client.module.render;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * X-Ray module that makes non-ore blocks transparent.
 * Actual block transparency is handled via MixinAbstractBlock which
 * checks this module's state. This class manages settings and triggers
 * chunk rebuilds on toggle.
 */
public class Xray extends Module {

    private final IntSetting opacity = settings.getDefaultGroup().add(
            new IntSetting.Builder().name("Opacity").description("Opacity of non-ore blocks (0 = invisible)").defaultValue(100).min(0).max(255).build()
    );

    /** Blocks that remain fully visible when Xray is active. */
    private static final Set<Block> VISIBLE_BLOCKS = new HashSet<>(Arrays.asList(
            Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE,
            Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE,
            Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE,
            Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE,
            Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE,
            Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE,
            Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE,
            Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE,
            Blocks.ANCIENT_DEBRIS,
            Blocks.CHEST, Blocks.ENDER_CHEST, Blocks.TRAPPED_CHEST,
            Blocks.SPAWNER,
            Blocks.LAVA, Blocks.WATER,
            Blocks.TNT,
            Blocks.OBSIDIAN
    ));

    public Xray() {
        super("Xray", "See through blocks to find ores", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        rebuildChunks();
    }

    @Override
    protected void onDisable() {
        rebuildChunks();
    }

    private void rebuildChunks() {
        if (mc.worldRenderer != null) {
            mc.worldRenderer.reload();
        }
    }

    /**
     * Checks whether a block should remain fully visible (not made transparent).
     * Used by MixinAbstractBlock.
     */
    public boolean isBlockVisible(Block block) {
        return VISIBLE_BLOCKS.contains(block);
    }

    public boolean isVisibleBlock(Block block) {
        return isBlockVisible(block);
    }

    public int getOpacity() {
        return opacity.get();
    }
}
