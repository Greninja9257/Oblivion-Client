package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.entity.effect.StatusEffects;

public final class WurstNoLevitation extends Module {
    public WurstNoLevitation() {
        super("NoLevitation", "Removes levitation effect every tick.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player != null) {
            mc.player.removeStatusEffect(StatusEffects.LEVITATION);
        }
    }
}
