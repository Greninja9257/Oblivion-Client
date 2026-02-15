package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public final class WurstMultiAura extends Module {
    private final DoubleSetting range = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Range").description("Attack range").defaultValue(4.2).range(1.0, 7.0).build()
    );

    private final IntSetting maxTargets = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Max Targets").description("Max entities per swing").defaultValue(3).range(1, 10).build()
    );

    public WurstMultiAura() {
        super("MultiAura", "Attacks multiple nearby entities each cooldown.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (mc.player.getAttackCooldownProgress(0.5f) < 1f) return;

        int hits = 0;
        for (Entity e : mc.world.getEntities()) {
            if (hits >= maxTargets.get()) break;
            if (!(e instanceof LivingEntity l) || l == mc.player || !l.isAlive()) continue;
            if (mc.player.distanceTo(l) > range.get()) continue;

            mc.interactionManager.attackEntity(mc.player, l);
            hits++;
        }

        if (hits > 0) mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
    }
}
