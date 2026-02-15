package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public final class WurstAutoPotion extends Module {
    private final DoubleSetting healthThreshold = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Health").description("Use potion below this HP").defaultValue(10.0).range(1.0, 36.0).build()
    );

    public WurstAutoPotion() {
        super("AutoPotion", "Automatically uses splash/lingering healing potions.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() > healthThreshold.get()) return;

        int slot = findPotion();
        if (slot == -1) return;

        int prev = mc.player.getInventory().selectedSlot;
        mc.player.getInventory().selectedSlot = slot;
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        mc.player.swingHand(Hand.MAIN_HAND);
        mc.player.getInventory().selectedSlot = prev;
    }

    private int findPotion() {
        for (int i = 0; i < 9; i++) {
            var item = mc.player.getInventory().getStack(i).getItem();
            if (item == Items.SPLASH_POTION || item == Items.LINGERING_POTION) return i;
        }
        return -1;
    }
}
