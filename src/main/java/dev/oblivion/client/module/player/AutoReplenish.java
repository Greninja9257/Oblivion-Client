package dev.oblivion.client.module.player;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class AutoReplenish extends Module {

    private final IntSetting threshold = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Threshold")
            .description("Refill when stack count drops to this value.")
            .defaultValue(8)
            .range(1, 63)
            .build()
    );

    public AutoReplenish() {
        super("AutoReplenish", "Automatically refills hotbar items from your inventory.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (mc.currentScreen != null) return;

        for (int i = 0; i < 9; i++) {
            ItemStack hotbarStack = mc.player.getInventory().getStack(i);
            if (hotbarStack.isEmpty()) continue;
            if (!hotbarStack.isStackable()) continue;
            if (hotbarStack.getCount() > threshold.get()) continue;

            // Find matching item in main inventory
            for (int j = 9; j < 36; j++) {
                ItemStack invStack = mc.player.getInventory().getStack(j);
                if (invStack.isEmpty()) continue;
                if (!ItemStack.areItemsEqual(hotbarStack, invStack)) continue;

                // Move to hotbar
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, j, i, SlotActionType.SWAP, mc.player);
                return;
            }
        }
    }
}
