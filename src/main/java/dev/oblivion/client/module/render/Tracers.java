package dev.oblivion.client.module.render;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.ColorSetting;
import dev.oblivion.client.util.RenderUtil;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class Tracers extends Module {

    private final BoolSetting players = settings.getDefaultGroup().add(
            new BoolSetting.Builder().name("Players").description("Trace lines to players").defaultValue(true).build()
    );
    private final BoolSetting mobs = settings.getDefaultGroup().add(
            new BoolSetting.Builder().name("Mobs").description("Trace lines to hostile mobs").defaultValue(true).build()
    );
    private final ColorSetting color = settings.getDefaultGroup().add(
            new ColorSetting.Builder().name("Color").description("Tracer line color").defaultValue(255, 255, 255, 255).build()
    );

    public Tracers() {
        super("Tracers", "Draws lines from crosshair to entities", Category.RENDER);
    }

    @EventHandler
    public void onWorldRender(RenderEvent.World event) {
        if (mc.world == null || mc.player == null) return;

        MatrixStack matrices = event.getMatrices();
        float tickDelta = event.getTickDelta();
        Vec3d camPos = mc.gameRenderer.getCamera().getPos();

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;

        RenderUtil.setupRenderState(1.5f);

        Vec3d lookDir = Vec3d.fromPolar(mc.gameRenderer.getCamera().getPitch(), mc.gameRenderer.getCamera().getYaw());

        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            if (!shouldTrace(entity)) continue;

            double entityX = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()) - camPos.x;
            double entityY = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()) - camPos.y + entity.getHeight() / 2.0;
            double entityZ = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()) - camPos.z;

            matrices.push();

            Matrix4f matrix = matrices.peek().getPositionMatrix();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

            buffer.vertex(matrix, (float) lookDir.x, (float) lookDir.y, (float) lookDir.z).color(r, g, b, a);
            buffer.vertex(matrix, (float) entityX, (float) entityY, (float) entityZ).color(r, g, b, a);

            BufferRenderer.drawWithGlobalProgram(buffer.end());

            matrices.pop();
        }

        RenderUtil.teardownRenderState();
    }

    private boolean shouldTrace(Entity entity) {
        if (entity instanceof PlayerEntity && players.get()) return true;
        if (entity instanceof HostileEntity && mobs.get()) return true;
        return false;
    }
}
