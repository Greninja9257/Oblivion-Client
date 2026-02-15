package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public final class WurstTpAura extends Module {
    private final DoubleSetting range = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Range").description("Teleport engage range").defaultValue(18.0).range(3.0, 64.0).build()
    );

    public WurstTpAura() {
        super("TpAura", "Teleports to hit distant targets, then returns.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (mc.player.getAttackCooldownProgress(0.5f) < 1f) return;

        LivingEntity target = null;
        double best = range.get() * range.get();
        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof LivingEntity l) || l == mc.player || !l.isAlive()) continue;
            double d = mc.player.squaredDistanceTo(l);
            if (d < best) {
                best = d;
                target = l;
            }
        }
        if (target == null) return;

        Vec3d old = mc.player.getPos();
        mc.player.setPosition(target.getX(), target.getY(), target.getZ());
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
        mc.player.setPosition(old);
    }
}
