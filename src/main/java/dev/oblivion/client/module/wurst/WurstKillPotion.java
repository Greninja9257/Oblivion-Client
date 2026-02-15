package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public final class WurstKillPotion extends Module {
    private final DoubleSetting range = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Range").description("Potion throw range").defaultValue(4.0).range(1.0, 8.0).build()
    );

    public WurstKillPotion() {
        super("KillPotion", "Throws harmful potions at nearby targets.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (mc.player.getMainHandStack().getItem() != Items.SPLASH_POTION) return;

        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof LivingEntity l) || l == mc.player || !l.isAlive()) continue;
            if (mc.player.distanceTo(l) <= range.get()) {
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                mc.player.swingHand(Hand.MAIN_HAND);
                break;
            }
        }
    }
}
