package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public final class WurstKillauraLegit extends Module {
    private final DoubleSetting range = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Range").description("Legit aura range").defaultValue(3.4).range(1.0, 6.0).build()
    );

    private final IntSetting cps = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("CPS").description("Clicks per second").defaultValue(8).range(1, 20).build()
    );

    private long last;

    public WurstKillauraLegit() {
        super("KillauraLegit", "Low-profile aura with CPS limit.", Category.COMBAT);
    }

    @Override
    protected void onEnable() {
        last = 0L;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        long now = System.currentTimeMillis();
        long interval = Math.max(1L, 1000L / cps.get());
        if (now - last < interval) return;

        LivingEntity best = null;
        double bestDist = range.get() * range.get();
        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof LivingEntity l) || l == mc.player || !l.isAlive()) continue;
            double d = mc.player.squaredDistanceTo(l);
            if (d < bestDist) {
                bestDist = d;
                best = l;
            }
        }

        if (best != null) {
            mc.interactionManager.attackEntity(mc.player, best);
            mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
            last = now;
        }
    }
}
