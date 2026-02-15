package dev.oblivion.client.module.combat;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.DoubleSetting;
import dev.oblivion.client.setting.impl.EnumSetting;
import dev.oblivion.client.util.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KillAura extends Module {

    public enum Mode { SINGLE, MULTI }

    private final DoubleSetting range = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Range")
            .description("Attack range in blocks.")
            .defaultValue(4.5)
            .range(1.0, 6.0)
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

    private final EnumSetting<Mode> mode = settings.getDefaultGroup().add(
        new EnumSetting.Builder<Mode>()
            .name("Mode")
            .description("Single target or multi target.")
            .defaultValue(Mode.SINGLE)
            .build()
    );

    private final BoolSetting rotations = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Rotations")
            .description("Rotate toward the target before attacking.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting cooldownOnly = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Cooldown Only")
            .description("Only attack when the attack cooldown is ready.")
            .defaultValue(true)
            .build()
    );

    private final DoubleSetting attacksPerSecond = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Attacks Per Second")
            .description("Attack rate when cooldown-only mode is disabled.")
            .defaultValue(8.0)
            .range(1.0, 20.0)
            .visible(() -> !cooldownOnly.get())
            .build()
    );

    private final BoolSetting swingAnimation = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Swing Animation")
            .description("Play hand swing animation when attacking.")
            .defaultValue(true)
            .build()
    );

    private long lastAttackTimeMs = 0L;

    public KillAura() {
        super("KillAura", "Automatically attacks nearby entities.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        if (!canAttackNow()) return;

        List<LivingEntity> targets = getTargets();
        if (targets.isEmpty()) return;

        if (mode.get() == Mode.SINGLE) {
            LivingEntity target = targets.get(0);
            attackEntity(target);
        } else {
            for (LivingEntity target : targets) {
                attackEntity(target);
                // APS mode should only attack once per timer window.
                if (!cooldownOnly.get()) break;
                if (mc.player.getAttackCooldownProgress(0.5f) < 1.0f) break;
            }
        }
    }

    private boolean canAttackNow() {
        if (cooldownOnly.get()) {
            return mc.player.getAttackCooldownProgress(0.5f) >= 1.0f;
        }

        long now = System.currentTimeMillis();
        long intervalMs = Math.max(1L, (long) (1000.0 / attacksPerSecond.get()));
        if (now - lastAttackTimeMs < intervalMs) {
            return false;
        }

        lastAttackTimeMs = now;
        return true;
    }

    private List<LivingEntity> getTargets() {
        List<LivingEntity> targets = new ArrayList<>();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (!PlayerUtil.isValid(entity)) continue;
            if (PlayerUtil.distanceTo(entity) > range.get()) continue;

            if (entity instanceof PlayerEntity && !targetPlayers.get()) continue;
            if (entity instanceof Monster && !targetMobs.get()) continue;
            if (entity instanceof AnimalEntity && !targetAnimals.get()) continue;

            // Skip entities that are not targeted by any setting
            if (!(entity instanceof PlayerEntity) && !(entity instanceof Monster) && !(entity instanceof AnimalEntity)) {
                continue;
            }

            targets.add(living);
        }

        targets.sort(Comparator.comparingDouble(PlayerUtil::distanceTo));
        return targets;
    }

    private void attackEntity(LivingEntity target) {
        if (rotations.get()) {
            float[] rots = PlayerUtil.getRotationsTo(target);
            mc.player.setYaw(rots[0]);
            mc.player.setPitch(rots[1]);
        }

        mc.interactionManager.attackEntity(mc.player, target);
        if (swingAnimation.get()) {
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }
}
