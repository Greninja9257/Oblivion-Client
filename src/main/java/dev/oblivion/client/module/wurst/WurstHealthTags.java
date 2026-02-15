package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class WurstHealthTags extends Module {
    private final DoubleSetting scale = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Scale").description("Tag scale").defaultValue(1.0).range(0.5, 3.0).build()
    );

    public WurstHealthTags() {
        super("HealthTags", "Renders compact health tags above players.", Category.RENDER);
    }

    @EventHandler
    public void onRender(RenderEvent.World event) {
        if (mc.player == null || mc.world == null) return;

        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        float tickDelta = event.getTickDelta();

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;

            double x = MathHelper.lerp(tickDelta, player.lastRenderX, player.getX()) - cam.x;
            double y = MathHelper.lerp(tickDelta, player.lastRenderY, player.getY()) - cam.y + player.getHeight() + 0.4;
            double z = MathHelper.lerp(tickDelta, player.lastRenderZ, player.getZ()) - cam.z;

            float health = player.getHealth() + player.getAbsorptionAmount();
            int color = health > 14 ? 0xFF00FF66 : health > 8 ? 0xFFFFC83D : 0xFFFF5A5A;
            String text = String.format("%.1f❤", health);

            event.getMatrices().push();
            event.getMatrices().translate(x, y, z);
            event.getMatrices().multiply(mc.gameRenderer.getCamera().getRotation());
            float s = (float) (0.025 * scale.get());
            event.getMatrices().scale(-s, -s, s);

            int w = mc.textRenderer.getWidth(text);
            VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
            mc.textRenderer.draw(
                text,
                -w / 2f,
                0f,
                color,
                false,
                event.getMatrices().peek().getPositionMatrix(),
                immediate,
                TextRenderer.TextLayerType.NORMAL,
                0,
                0xF000F0
            );
            immediate.draw();
            event.getMatrices().pop();
        }
    }
}
