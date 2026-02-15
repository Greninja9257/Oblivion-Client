package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.ColorSetting;
import dev.oblivion.client.util.RenderUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class WurstPlayerEsp extends Module {
    private final ColorSetting color = settings.getDefaultGroup().add(
        new ColorSetting.Builder().name("Color").description("ESP color").defaultValue(255, 50, 50, 210).build()
    );

    public WurstPlayerEsp() {
        super("PlayerEsp", "Highlights other players.", Category.RENDER);
    }

    @EventHandler
    public void onRender(RenderEvent.World event) {
        if (mc.player == null || mc.world == null) return;

        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        float tickDelta = event.getTickDelta();
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;

        RenderUtil.setupRenderState(2f);
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;

            double x = MathHelper.lerp(tickDelta, player.lastRenderX, player.getX()) - cam.x;
            double y = MathHelper.lerp(tickDelta, player.lastRenderY, player.getY()) - cam.y;
            double z = MathHelper.lerp(tickDelta, player.lastRenderZ, player.getZ()) - cam.z;

            Box box = player.getBoundingBox().offset(-player.getX(), -player.getY(), -player.getZ());
            event.getMatrices().push();
            event.getMatrices().translate(x, y, z);
            RenderUtil.drawBoxOutline(event.getMatrices(),
                (float) box.minX, (float) box.minY, (float) box.minZ,
                (float) box.maxX, (float) box.maxY, (float) box.maxZ,
                r, g, b, a);
            event.getMatrices().pop();
        }
        RenderUtil.teardownRenderState();
    }
}
