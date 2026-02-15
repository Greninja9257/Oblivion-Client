package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.ColorSetting;
import dev.oblivion.client.util.RenderUtil;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class WurstItemEsp extends Module {
    private final ColorSetting color = settings.getDefaultGroup().add(
        new ColorSetting.Builder().name("Color").description("Item ESP color").defaultValue(50, 170, 255, 220).build()
    );

    public WurstItemEsp() {
        super("ItemEsp", "Highlights dropped items.", Category.RENDER);
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

        RenderUtil.setupRenderState(1.4f);
        for (ItemEntity item : mc.world.getEntitiesByClass(ItemEntity.class, mc.player.getBoundingBox().expand(96), e -> true)) {
            double x = MathHelper.lerp(tickDelta, item.lastRenderX, item.getX()) - cam.x;
            double y = MathHelper.lerp(tickDelta, item.lastRenderY, item.getY()) - cam.y;
            double z = MathHelper.lerp(tickDelta, item.lastRenderZ, item.getZ()) - cam.z;

            Box box = item.getBoundingBox().offset(-item.getX(), -item.getY(), -item.getZ());
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
