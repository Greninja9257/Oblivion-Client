package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public final class WurstTrollPotion extends Module {
    private int tick;

    public WurstTrollPotion() {
        super("TrollPotion", "Periodically throws held splash potion.", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (mc.player.getMainHandStack().getItem() != Items.SPLASH_POTION) return;

        if (++tick >= 30) {
            tick = 0;
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }
}
