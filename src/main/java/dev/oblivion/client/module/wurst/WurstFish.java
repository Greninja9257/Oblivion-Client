package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public final class WurstFish extends Module {
    private int ticks;

    public WurstFish() {
        super("Fish", "Simple auto-cast fishing helper.", Category.PLAYER);
    }

    @Override
    protected void onEnable() {
        ticks = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (mc.player.getMainHandStack().getItem() != Items.FISHING_ROD) return;

        if (++ticks >= 120) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
            ticks = 0;
        }
    }
}
