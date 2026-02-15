package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public final class WurstRestock extends Module {
    private final IntSetting minCount = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Min Count").description("Refill when stack goes below this").defaultValue(8).range(1, 63).build()
    );

    private final IntSetting interval = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Interval").description("Ticks between restock attempts").defaultValue(10).range(1, 100).build()
    );

    private int ticks;

    public WurstRestock() {
        super("Restock", "Refills low hotbar stacks from inventory.", Category.PLAYER);
    }

    @Override
    protected void onEnable() {
        ticks = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.interactionManager == null) return;

        ticks++;
        if (ticks < interval.get()) return;
        ticks = 0;

        for (int hotbar = 0; hotbar < 9; hotbar++) {
            ItemStack hotbarStack = mc.player.getInventory().getStack(hotbar);
            if (hotbarStack.isEmpty() || hotbarStack.getCount() >= minCount.get()) continue;

            int sourceInv = findMatchingInventoryStack(hotbarStack.getItem());
            if (sourceInv == -1) continue;

            int syncId = mc.player.currentScreenHandler.syncId;
            int from = sourceInv;
            int to = 36 + hotbar;

            mc.interactionManager.clickSlot(syncId, from, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(syncId, to, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(syncId, from, 0, SlotActionType.PICKUP, mc.player);
            break;
        }
    }

    private int findMatchingInventoryStack(Item item) {
        for (int slot = 9; slot < 36; slot++) {
            ItemStack stack = mc.player.getInventory().getStack(slot);
            if (!stack.isEmpty() && stack.getItem() == item) return slot;
        }
        return -1;
    }
}
