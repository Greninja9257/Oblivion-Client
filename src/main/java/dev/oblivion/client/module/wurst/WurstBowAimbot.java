package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;

public final class WurstBowAimbot extends Module {
    private final DoubleSetting range = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Range").description("Bow target range").defaultValue(30.0).range(4.0, 64.0).build()
    );

    public WurstBowAimbot() {
        super("BowAimbot", "Aims at targets while drawing a bow.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;
        if (mc.player.getMainHandStack().getItem() != Items.BOW) return;
        if (!mc.player.isUsingItem()) return;

        LivingEntity target = null;
        double bestDist = range.get() * range.get();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity living) || entity == mc.player || !entity.isAlive()) continue;
            double distSq = mc.player.squaredDistanceTo(entity);
            if (distSq <= bestDist) {
                bestDist = distSq;
                target = living;
            }
        }

        if (target == null) return;

        double dx = target.getX() - mc.player.getX();
        double dz = target.getZ() - mc.player.getZ();
        double dy = target.getEyeY() - mc.player.getEyeY();
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));

        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
    }
}
