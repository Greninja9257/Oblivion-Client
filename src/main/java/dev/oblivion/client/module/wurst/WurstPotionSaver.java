package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;

public final class WurstPotionSaver extends Module {
    private final IntSetting stopBelowFood = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Stop Sprint Below Food").description("Disable sprint to preserve effects").defaultValue(6).range(0, 20).build()
    );

    public WurstPotionSaver() {
        super("PotionSaver", "Avoids unnecessary sprinting while effects are active.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        if (mc.player.getStatusEffects().isEmpty()) return;

        if (mc.player.getHungerManager().getFoodLevel() <= stopBelowFood.get()) {
            mc.player.setSprinting(false);
        }
    }
}
