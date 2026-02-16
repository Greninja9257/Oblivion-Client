package dev.oblivion.client.module.combat;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class Trigger extends Module {

    private final BoolSetting cooldownOnly = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Cooldown Only")
            .description("Only attack when the attack cooldown is ready.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting targetPlayers = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Target Players")
            .description("Attack players.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting targetMobs = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Target Mobs")
            .description("Attack hostile mobs.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting targetAnimals = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Target Animals")
            .description("Attack passive animals.")
            .defaultValue(false)
            .build()
    );

    public Trigger() {
        super("Trigger", "Automatically attacks the entity you are looking at.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.ENTITY) return;

        Entity entity = ((EntityHitResult) mc.crosshairTarget).getEntity();
        if (!(entity instanceof LivingEntity living) || !entity.isAlive() || entity == mc.player) return;

        if (entity instanceof PlayerEntity && !targetPlayers.get()) return;
        if (entity instanceof Monster && !targetMobs.get()) return;
        if (entity instanceof AnimalEntity && !targetAnimals.get()) return;

        if (cooldownOnly.get() && mc.player.getAttackCooldownProgress(0.5f) < 1.0f) return;

        mc.interactionManager.attackEntity(mc.player, entity);
        mc.player.swingHand(Hand.MAIN_HAND);
    }
}
