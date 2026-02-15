package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public final class WurstThrow extends Module {
    public WurstThrow() {
        super("Throw", "Auto-throws throwable items from main hand.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.interactionManager == null) return;

        var item = mc.player.getMainHandStack().getItem();
        if (item == Items.ENDER_PEARL || item == Items.SNOWBALL || item == Items.EGG || item == Items.EXPERIENCE_BOTTLE) {
            if (mc.options.useKey.isPressed()) {
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }
}
