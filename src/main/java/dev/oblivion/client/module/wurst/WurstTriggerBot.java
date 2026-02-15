package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;

public final class WurstTriggerBot extends Module {
    private final IntSetting cps = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("CPS").description("Clicks per second").defaultValue(10).range(1, 20).build()
    );

    private long lastAttack;

    public WurstTriggerBot() {
        super("TriggerBot", "Automatically attacks when your crosshair is on a target.", Category.COMBAT);
    }

    @Override
    protected void onEnable() {
        lastAttack = 0L;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        long now = System.currentTimeMillis();
        long interval = Math.max(1, 1000 / cps.get());
        if (now - lastAttack < interval) return;

        if (mc.crosshairTarget instanceof EntityHitResult hit) {
            Entity entity = hit.getEntity();
            if (entity instanceof LivingEntity && entity != mc.player && entity.isAlive()) {
                mc.interactionManager.attackEntity(mc.player, entity);
                mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
                lastAttack = now;
            }
        }
    }
}
