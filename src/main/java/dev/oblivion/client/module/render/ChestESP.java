package dev.oblivion.client.module.render;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.ColorSetting;
import dev.oblivion.client.util.RenderUtil;
import net.minecraft.block.entity.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;

public class ChestESP extends Module {

    private final BoolSetting chests = settings.getDefaultGroup().add(
            new BoolSetting.Builder().name("Chests").description("Highlight regular chests").defaultValue(true).build()
    );
    private final BoolSetting enderChests = settings.getDefaultGroup().add(
            new BoolSetting.Builder().name("EnderChests").description("Highlight ender chests").defaultValue(true).build()
    );
    private final BoolSetting shulkers = settings.getDefaultGroup().add(
            new BoolSetting.Builder().name("Shulkers").description("Highlight shulker boxes").defaultValue(true).build()
    );
    private final ColorSetting color = settings.getDefaultGroup().add(
            new ColorSetting.Builder().name("Color").description("ESP box color").defaultValue(255, 200, 0, 255).build()
    );

    public ChestESP() {
        super("ChestESP", "Highlights storage block entities", Category.RENDER);
    }

    @EventHandler
    public void onWorldRender(RenderEvent.World event) {
        if (mc.world == null || mc.player == null) return;

        MatrixStack matrices = event.getMatrices();
        Vec3d camPos = mc.gameRenderer.getCamera().getPos();

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;

        RenderUtil.setupRenderState(2f);

        int viewDistance = mc.options.getViewDistance().getValue();
        int chunkX = mc.player.getChunkPos().x;
        int chunkZ = mc.player.getChunkPos().z;

        for (int cx = chunkX - viewDistance; cx <= chunkX + viewDistance; cx++) {
            for (int cz = chunkZ - viewDistance; cz <= chunkZ + viewDistance; cz++) {
                WorldChunk chunk = mc.world.getChunk(cx, cz);
                if (chunk == null) continue;

                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (!shouldRender(blockEntity)) continue;

                    BlockPos pos = blockEntity.getPos();
                    double x = pos.getX() - camPos.x;
                    double y = pos.getY() - camPos.y;
                    double z = pos.getZ() - camPos.z;

                    matrices.push();
                    matrices.translate(x, y, z);
                    RenderUtil.drawBoxOutline(matrices, 0, 0, 0, 1, 1, 1, r, g, b, a);
                    matrices.pop();
                }
            }
        }

        RenderUtil.teardownRenderState();
    }

    private boolean shouldRender(BlockEntity blockEntity) {
        if (blockEntity instanceof ChestBlockEntity && chests.get()) return true;
        if (blockEntity instanceof TrappedChestBlockEntity && chests.get()) return true;
        if (blockEntity instanceof EnderChestBlockEntity && enderChests.get()) return true;
        if (blockEntity instanceof ShulkerBoxBlockEntity && shulkers.get()) return true;
        return false;
    }
}
