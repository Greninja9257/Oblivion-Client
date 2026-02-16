package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.util.math.Vec3d;

public class ReverseStep extends Module {

    private final DoubleSetting height = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Height")
            .description("Maximum step-down height.")
            .defaultValue(2.0)
            .range(0.5, 10.0)
            .build()
    );

    public ReverseStep() {
        super("ReverseStep", "Allows you to step down blocks quickly.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;
        if (!mc.player.isOnGround()) return;
        if (mc.player.isSneaking() || mc.options.jumpKey.isPressed()) return;

        // Check if there's a drop below
        if (mc.world.getBlockState(mc.player.getBlockPos().down()).isAir()) {
            Vec3d vel = mc.player.getVelocity();
            mc.player.setVelocity(vel.x, -height.get() * 0.5, vel.z);
        }
    }
}
