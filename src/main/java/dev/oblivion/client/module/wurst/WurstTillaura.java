package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public final class WurstTillaura extends Module {
    private final DoubleSetting range = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Range").description("Aura range").defaultValue(4.6).range(1.0, 8.0).build()
    );

    public WurstTillaura() {
        super("Tillaura", "Aggressive aura tuned for very fast attacks.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof LivingEntity l) || l == mc.player || !l.isAlive()) continue;
            if (mc.player.distanceTo(l) > range.get()) continue;
            mc.interactionManager.attackEntity(mc.player, l);
            mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
            break;
        }
    }
}
