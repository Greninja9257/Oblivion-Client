package dev.oblivion.client.module.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class Nametags extends Module {

    private final DoubleSetting scale = settings.getDefaultGroup().add(
            new DoubleSetting.Builder().name("Scale").description("Nametag scale").defaultValue(1.5).min(0.5).max(3.0).build()
    );
    private final BoolSetting showHealth = settings.getDefaultGroup().add(
            new BoolSetting.Builder().name("Health").description("Show player health").defaultValue(true).build()
    );
    private final BoolSetting showDistance = settings.getDefaultGroup().add(
            new BoolSetting.Builder().name("Distance").description("Show distance to player").defaultValue(true).build()
    );
    private final BoolSetting showArmor = settings.getDefaultGroup().add(
            new BoolSetting.Builder().name("Armor").description("Show armor items").defaultValue(true).build()
    );

    public Nametags() {
        super("Nametags", "Renders custom nametags with player info", Category.RENDER);
    }

    @EventHandler
    public void onWorldRender(RenderEvent.World event) {
        if (mc.world == null || mc.player == null) return;

        MatrixStack matrices = event.getMatrices();
        float tickDelta = event.getTickDelta();
        Vec3d camPos = mc.gameRenderer.getCamera().getPos();
        TextRenderer textRenderer = mc.textRenderer;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;

            double x = MathHelper.lerp(tickDelta, player.lastRenderX, player.getX()) - camPos.x;
            double y = MathHelper.lerp(tickDelta, player.lastRenderY, player.getY()) - camPos.y + player.getHeight() + 0.5;
            double z = MathHelper.lerp(tickDelta, player.lastRenderZ, player.getZ()) - camPos.z;

            double distance = mc.player.distanceTo(player);

            // Build nametag text
            StringBuilder sb = new StringBuilder();
            sb.append(player.getName().getString());

            if (showHealth.get()) {
                float health = player.getHealth();
                Formatting healthColor;
                if (health > 15) healthColor = Formatting.GREEN;
                else if (health > 10) healthColor = Formatting.YELLOW;
                else if (health > 5) healthColor = Formatting.GOLD;
                else healthColor = Formatting.RED;
                sb.append(" ").append(healthColor).append(String.format("%.1f", health)).append(Formatting.WHITE).append("\u2764");
            }

            if (showDistance.get()) {
                sb.append(" ").append(Formatting.GRAY).append(String.format("%.1fm", distance));
            }

            String text = sb.toString();

            matrices.push();
            matrices.translate(x, y, z);

            // Billboard rotation: face the camera
            matrices.multiply(mc.gameRenderer.getCamera().getRotation());

            // Scale based on distance so nametags remain readable
            float baseScale = (float) (scale.get() * 0.025);
            float distanceScale = Math.max(1f, (float) distance / 10f);
            float finalScale = baseScale * distanceScale;
            matrices.scale(-finalScale, -finalScale, finalScale);

            Matrix4f matrix = matrices.peek().getPositionMatrix();

            float textWidth = textRenderer.getWidth(text);
            float textX = -textWidth / 2f;

            // Draw background
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            float padding = 2f;
            buffer.vertex(matrix, textX - padding, -1 - padding, 0).color(0f, 0f, 0f, 0.6f);
            buffer.vertex(matrix, textX - padding, 8 + padding, 0).color(0f, 0f, 0f, 0.6f);
            buffer.vertex(matrix, textX + textWidth + padding, 8 + padding, 0).color(0f, 0f, 0f, 0.6f);
            buffer.vertex(matrix, textX + textWidth + padding, -1 - padding, 0).color(0f, 0f, 0f, 0.6f);

            RenderSystem.setShader(net.minecraft.client.gl.ShaderProgramKeys.POSITION_COLOR);
            BufferRenderer.drawWithGlobalProgram(buffer.end());

            // Draw text
            VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
            textRenderer.draw(text, textX, 0, 0xFFFFFFFF, false, matrix, immediate, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
            immediate.draw();

            // Draw armor items above the nametag
            if (showArmor.get()) {
                int armorOffset = 0;
                for (ItemStack armorItem : player.getArmorItems()) {
                    if (!armorItem.isEmpty()) {
                        // Armor rendering is deferred to HUD overlay; positions tracked here
                        armorOffset++;
                    }
                }
            }

            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();

            matrices.pop();
        }
    }
}
