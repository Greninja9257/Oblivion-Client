package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.util.RenderUtil;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class WurstProphuntEsp extends Module {
    public WurstProphuntEsp() {
        super("ProphuntEsp", "Highlights likely prop-hunt disguise entities.", Category.RENDER);
    }

    @EventHandler
    public void onRender(RenderEvent.World event) {
        if (mc.player == null || mc.world == null) return;

        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        float tick = event.getTickDelta();

        RenderUtil.setupRenderState(1.6f);
        for (var e : mc.world.getEntities()) {
            if (!(e instanceof ArmorStandEntity) && !(e instanceof ItemFrameEntity)) continue;

            double x = MathHelper.lerp(tick, e.lastRenderX, e.getX()) - cam.x;
            double y = MathHelper.lerp(tick, e.lastRenderY, e.getY()) - cam.y;
            double z = MathHelper.lerp(tick, e.lastRenderZ, e.getZ()) - cam.z;
            Box b = e.getBoundingBox().offset(-e.getX(), -e.getY(), -e.getZ());

            event.getMatrices().push();
            event.getMatrices().translate(x, y, z);
            RenderUtil.drawBoxOutline(event.getMatrices(), (float)b.minX, (float)b.minY, (float)b.minZ, (float)b.maxX, (float)b.maxY, (float)b.maxZ, 1f, 0.6f, 0.1f, 0.95f);
            event.getMatrices().pop();
        }
        RenderUtil.teardownRenderState();
    }
}
