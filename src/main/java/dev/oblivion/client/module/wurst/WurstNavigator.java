package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;

public final class WurstNavigator extends Module {
    private final DoubleSetting targetX = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Target X").description("Destination X coordinate").defaultValue(0).range(-30000000, 30000000).build()
    );

    private final DoubleSetting targetZ = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Target Z").description("Destination Z coordinate").defaultValue(0).range(-30000000, 30000000).build()
    );

    private final DoubleSetting speed = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Speed").description("Move speed toward target").defaultValue(0.22).range(0.05, 1.0).build()
    );

    public WurstNavigator() {
        super("Navigator", "Moves toward configured X/Z coordinates.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        double dx = targetX.get() - mc.player.getX();
        double dz = targetZ.get() - mc.player.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);

        if (dist < 1.2) {
            mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
            return;
        }

        double nx = dx / dist;
        double nz = dz / dist;
        mc.player.setVelocity(nx * speed.get(), mc.player.getVelocity().y, nz * speed.get());

        float yaw = (float) Math.toDegrees(Math.atan2(nz, nx)) - 90f;
        mc.player.setYaw(yaw);
    }
}
