package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.ColorSetting;
import dev.oblivion.client.util.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class WurstMobEsp extends Module {
    private final BoolSetting hostiles = settings.getDefaultGroup().add(
        new BoolSetting.Builder().name("Hostiles").description("Show hostile mobs").defaultValue(true).build()
    );

    private final BoolSetting animals = settings.getDefaultGroup().add(
        new BoolSetting.Builder().name("Animals").description("Show passive animals").defaultValue(false).build()
    );

    private final ColorSetting hostileColor = settings.getDefaultGroup().add(
        new ColorSetting.Builder().name("Hostile Color").description("Hostile color").defaultValue(255, 128, 0, 210).build()
    );

    private final ColorSetting animalColor = settings.getDefaultGroup().add(
        new ColorSetting.Builder().name("Animal Color").description("Animal color").defaultValue(50, 255, 120, 210).build()
    );

    public WurstMobEsp() {
        super("MobEsp", "Highlights nearby mobs.", Category.RENDER);
    }

    @EventHandler
    public void onRender(RenderEvent.World event) {
        if (mc.player == null || mc.world == null) return;

        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        float tickDelta = event.getTickDelta();
        RenderUtil.setupRenderState(1.8f);

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof HostileEntity) && !(entity instanceof AnimalEntity)) continue;

            boolean render = (entity instanceof HostileEntity && hostiles.get()) || (entity instanceof AnimalEntity && animals.get());
            if (!render) continue;

            ColorSetting c = (entity instanceof HostileEntity) ? hostileColor : animalColor;
            float r = c.getRed() / 255f;
            float g = c.getGreen() / 255f;
            float b = c.getBlue() / 255f;
            float a = c.getAlpha() / 255f;

            double x = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()) - cam.x;
            double y = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()) - cam.y;
            double z = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()) - cam.z;
            Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());

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
