package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public final class WurstFightBot extends Module {
    private final DoubleSetting range = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Range").description("Fight target range").defaultValue(4.2).range(1.0, 7.0).build()
    );

    private LivingEntity target;

    public WurstFightBot() {
        super("FightBot", "Automatically fights nearest hostile target.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        if (target == null || !target.isAlive() || mc.player.distanceTo(target) > range.get()) {
            target = null;
            double best = range.get() * range.get();
            for (Entity e : mc.world.getEntities()) {
                if (!(e instanceof LivingEntity l) || l == mc.player || !l.isAlive()) continue;
                double d = mc.player.squaredDistanceTo(l);
                if (d < best) {
                    best = d;
                    target = l;
                }
            }
        }

        if (target == null) return;

        if (mc.player.getAttackCooldownProgress(0.5f) >= 1f) {
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
        }
    }
}
