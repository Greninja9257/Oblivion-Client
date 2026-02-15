package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;

public final class WurstFlight extends Module {
    private final DoubleSetting speed = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Speed").description("Free flight speed").defaultValue(0.45).range(0.05, 2.0).build()
    );

    public WurstFlight() {
        super("Flight", "Free flight movement.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        mc.player.noClip = true;

        double y = 0;
        if (mc.options.jumpKey.isPressed()) y += speed.get();
        if (mc.options.sneakKey.isPressed()) y -= speed.get();

        double forward = mc.player.input.movementForward;
        double strafe = mc.player.input.movementSideways;
        double yaw = Math.toRadians(mc.player.getYaw());

        double x = (-Math.sin(yaw) * forward + Math.cos(yaw) * strafe) * speed.get();
        double z = ( Math.cos(yaw) * forward + Math.sin(yaw) * strafe) * speed.get();
        mc.player.setVelocity(x, y, z);
    }

    @Override
    protected void onDisable() {
        if (mc.player != null) {
            mc.player.noClip = false;
        }
    }
}
