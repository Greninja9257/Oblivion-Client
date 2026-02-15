package dev.oblivion.client.module.render;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.ColorSetting;
import dev.oblivion.client.setting.impl.EnumSetting;
import dev.oblivion.client.util.RenderUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ESP extends Module {
    public enum RenderMode { BOX, GLOW }

    private final BoolSetting players = settings.getDefaultGroup().add(
            new BoolSetting.Builder().name("Players").description("Highlight players").defaultValue(true).build()
    );
    private final BoolSetting mobs = settings.getDefaultGroup().add(
            new BoolSetting.Builder().name("Mobs").description("Highlight hostile mobs").defaultValue(true).build()
    );
    private final BoolSetting animals = settings.getDefaultGroup().add(
            new BoolSetting.Builder().name("Animals").description("Highlight passive animals").defaultValue(false).build()
    );
    private final EnumSetting<RenderMode> mode = settings.getDefaultGroup().add(
            new EnumSetting.Builder<RenderMode>().name("Mode").description("Rendering style").defaultValue(RenderMode.BOX).build()
    );
    private final ColorSetting playerColor = settings.getDefaultGroup().add(
            new ColorSetting.Builder().name("Player Color").description("ESP color for players").defaultValue(255, 0, 0, 255).build()
    );
    private final ColorSetting mobColor = settings.getDefaultGroup().add(
            new ColorSetting.Builder().name("Mob Color").description("ESP color for hostile mobs").defaultValue(255, 140, 0, 255).build()
    );
    private final ColorSetting animalColor = settings.getDefaultGroup().add(
            new ColorSetting.Builder().name("Animal Color").description("ESP color for passive animals").defaultValue(0, 220, 120, 255).build()
    );

    public ESP() {
        super("ESP", "Draws box outlines around entities", Category.RENDER);
    }

    @EventHandler
    public void onWorldRender(RenderEvent.World event) {
        if (mc.world == null || mc.player == null) return;

        MatrixStack matrices = event.getMatrices();
        float tickDelta = event.getTickDelta();
        Vec3d camPos = mc.gameRenderer.getCamera().getPos();

        float lineWidth = mode.get() == RenderMode.GLOW ? 3.2f : 2.0f;
        RenderUtil.setupRenderState(lineWidth);

        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            if (!shouldRender(entity)) continue;

            ColorSetting entityColor = getEntityColor(entity);
            if (entityColor == null) continue;

            float r = entityColor.getRed() / 255f;
            float g = entityColor.getGreen() / 255f;
            float b = entityColor.getBlue() / 255f;
            float a = entityColor.getAlpha() / 255f;

            double x = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()) - camPos.x;
            double y = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()) - camPos.y;
            double z = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()) - camPos.z;

            Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());

            matrices.push();
            matrices.translate(x, y, z);

            if (mode.get() == RenderMode.GLOW) {
                RenderUtil.drawBoxOutline(matrices,
                        (float) box.minX, (float) box.minY, (float) box.minZ,
                        (float) box.maxX, (float) box.maxY, (float) box.maxZ,
                        r, g, b, Math.min(1.0f, a * 0.45f));
            }

            RenderUtil.drawBoxOutline(matrices,
                    (float) box.minX, (float) box.minY, (float) box.minZ,
                    (float) box.maxX, (float) box.maxY, (float) box.maxZ,
                    r, g, b, a);
            matrices.pop();
        }

        RenderUtil.teardownRenderState();
    }

    private boolean shouldRender(Entity entity) {
        if (entity instanceof PlayerEntity && players.get()) return true;
        if (entity instanceof HostileEntity && mobs.get()) return true;
        if (entity instanceof AnimalEntity && animals.get()) return true;
        return false;
    }

    private ColorSetting getEntityColor(Entity entity) {
        if (entity instanceof PlayerEntity) return playerColor;
        if (entity instanceof HostileEntity) return mobColor;
        if (entity instanceof AnimalEntity) return animalColor;
        return null;
    }
}
