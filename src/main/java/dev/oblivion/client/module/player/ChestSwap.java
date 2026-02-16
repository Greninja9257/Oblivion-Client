package dev.oblivion.client.module.player;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.util.ChatUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class ChestSwap extends Module {

    public ChestSwap() {
        super("ChestSwap", "Swaps between chestplate and elytra.", Category.PLAYER);
    }

    @Override
    protected void onEnable() {
        if (mc.player == null || mc.interactionManager == null) {
            disable();
            return;
        }

        // Chest armor is slot 38 in the player's inventory (armor slot index 2)
        ItemStack chest = mc.player.getInventory().getStack(38);
        boolean hasElytra = chest.getItem() == Items.ELYTRA;

        if (hasElytra) {
            int slot = findChestplate();
            if (slot == -1) {
                ChatUtil.error("No chestplate found in inventory.");
            } else {
                swap(slot);
                ChatUtil.info("Swapped to chestplate.");
            }
        } else {
            int slot = findElytra();
            if (slot == -1) {
                ChatUtil.error("No elytra found in inventory.");
            } else {
                swap(slot);
                ChatUtil.info("Swapped to elytra.");
            }
        }

        disable();
    }

    private void swap(int inventorySlot) {
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, inventorySlot, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 6, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, inventorySlot, 0, SlotActionType.PICKUP, mc.player);
    }

    private int findChestplate() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            String id = net.minecraft.registry.Registries.ITEM.getId(stack.getItem()).getPath();
            if (id.endsWith("_chestplate")) {
                return i < 9 ? i + 36 : i;
            }
        }
        return -1;
    }

    private int findElytra() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == Items.ELYTRA) {
                return i < 9 ? i + 36 : i;
            }
        }
        return -1;
    }
}
