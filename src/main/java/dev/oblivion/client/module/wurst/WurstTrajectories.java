package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.ColorSetting;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.util.RenderUtil;
import net.minecraft.util.math.Vec3d;

public final class WurstTrajectories extends Module {
    private final IntSetting steps = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Steps").description("Simulation steps").defaultValue(35).range(8, 120).build()
    );

    private final ColorSetting color = settings.getDefaultGroup().add(
        new ColorSetting.Builder().name("Color").description("Trajectory color").defaultValue(255, 255, 0, 220).build()
    );

    public WurstTrajectories() {
        super("Trajectories", "Renders simple projectile trajectory previews.", Category.RENDER);
    }

    @EventHandler
    public void onRender(RenderEvent.World event) {
        if (mc.player == null || mc.world == null) return;

        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        Vec3d start = mc.player.getEyePos().subtract(cam);
        Vec3d velocity = mc.player.getRotationVec(1.0f).multiply(0.9);

        int argb = color.get();
        Vec3d prev = start;
        Vec3d current = start;

        for (int i = 0; i < steps.get(); i++) {
            velocity = velocity.add(0, -0.05, 0).multiply(0.99);
            current = current.add(velocity);
            RenderUtil.drawLine(event.getMatrices(), prev, current, argb);
            prev = current;

            if (current.y < -64) break;
        }
    }
}
