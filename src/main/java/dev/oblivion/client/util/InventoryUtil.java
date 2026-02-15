package dev.oblivion.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class InventoryUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static int findInInventory(Item item) {
        if (mc.player == null) return -1;
        for (int i = 0; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == item) return i;
        }
        return -1;
    }

    public static int findInHotbar(Item item) {
        if (mc.player == null) return -1;
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == item) return i;
        }
        return -1;
    }

    public static void swapSlots(int from, int to) {
        if (mc.player == null || mc.interactionManager == null) return;
        int syncId = mc.player.currentScreenHandler.syncId;
        mc.interactionManager.clickSlot(syncId, from, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(syncId, to, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(syncId, from, 0, SlotActionType.PICKUP, mc.player);
    }

    public static void moveToOffhand(int slot) {
        if (mc.player == null || mc.interactionManager == null) return;
        int syncId = mc.player.currentScreenHandler.syncId;
        mc.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(syncId, 45, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, mc.player);
    }

    public static ItemStack getOffhandStack() {
        if (mc.player == null) return ItemStack.EMPTY;
        return mc.player.getOffHandStack();
    }
}
