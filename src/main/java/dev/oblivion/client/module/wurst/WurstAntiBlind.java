package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.entity.effect.StatusEffects;

public final class WurstAntiBlind extends Module {
    public WurstAntiBlind() {
        super("AntiBlind", "Removes blindness and darkness effects.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        mc.player.removeStatusEffect(StatusEffects.BLINDNESS);
        mc.player.removeStatusEffect(StatusEffects.DARKNESS);
    }
}
