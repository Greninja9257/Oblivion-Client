package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;

public final class WurstClickAura extends Module {
    private final BoolSetting cooldown = settings.getDefaultGroup().add(
        new BoolSetting.Builder().name("Cooldown").description("Respect vanilla attack cooldown").defaultValue(true).build()
    );

    public WurstClickAura() {
        super("ClickAura", "Attacks targeted entity while attack key is held.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (!mc.options.attackKey.isPressed()) return;
        if (cooldown.get() && mc.player.getAttackCooldownProgress(0.5f) < 1f) return;

        if (mc.crosshairTarget instanceof EntityHitResult hit) {
            Entity entity = hit.getEntity();
            if (entity instanceof LivingEntity && entity.isAlive() && entity != mc.player) {
                mc.interactionManager.attackEntity(mc.player, entity);
                mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
            }
        }
    }
}
