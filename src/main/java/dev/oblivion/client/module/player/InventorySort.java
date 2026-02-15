package dev.oblivion.client.module.player;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.EnumSetting;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InventorySort extends Module {

    public enum SortMode { TYPE, NAME }

    private final EnumSetting<SortMode> mode = settings.getDefaultGroup().add(
        new EnumSetting.Builder<SortMode>()
            .name("Mode")
            .description("How to sort the inventory.")
            .defaultValue(SortMode.TYPE)
            .build()
    );

    private boolean sorted = false;

    public InventorySort() {
        super("InventorySort", "Automatically sorts your inventory.", Category.PLAYER);
    }

    @Override
    protected void onEnable() {
        sorted = false;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (sorted) return;

        sortInventory();
        sorted = true;
    }

    public void sortInventory() {
        if (mc.player == null || mc.interactionManager == null) return;

        // Inventory slots 9-35 are the main inventory (excluding hotbar 0-8)
        List<SlotEntry> entries = new ArrayList<>();
        for (int i = 9; i <= 35; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty()) {
                entries.add(new SlotEntry(i, stack));
            }
        }

        Comparator<SlotEntry> comparator;
        if (mode.get() == SortMode.NAME) {
            comparator = Comparator.comparing(e -> e.stack.getName().getString());
        } else {
            // Sort by item ID (groups same item types together), then by count descending
            comparator = Comparator.comparing((SlotEntry e) ->
                Registries.ITEM.getId(e.stack.getItem()).toString()
            ).thenComparing((SlotEntry e) -> -e.stack.getCount());
        }

        entries.sort(comparator);

        // Perform swaps to arrange items into sorted order
        int syncId = mc.player.currentScreenHandler.syncId;
        List<Integer> targetSlots = new ArrayList<>();
        for (int i = 9; i <= 35; i++) {
            targetSlots.add(i);
        }

        for (int i = 0; i < entries.size(); i++) {
            SlotEntry entry = entries.get(i);
            int targetSlot = targetSlots.get(i);
            if (entry.slot != targetSlot) {
                // Swap via pick up and place
                mc.interactionManager.clickSlot(syncId, entry.slot, 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(syncId, targetSlot, 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(syncId, entry.slot, 0, SlotActionType.PICKUP, mc.player);

                // Update references for subsequent swaps
                for (int j = i + 1; j < entries.size(); j++) {
                    if (entries.get(j).slot == targetSlot) {
                        entries.get(j).slot = entry.slot;
                        break;
                    }
                }
            }
        }
    }

    private static class SlotEntry {
        int slot;
        final ItemStack stack;

        SlotEntry(int slot, ItemStack stack) {
            this.slot = slot;
            this.stack = stack;
        }
    }
}
