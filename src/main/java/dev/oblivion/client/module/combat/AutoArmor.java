package dev.oblivion.client.module.combat;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmor extends Module {

    // Armor slot indices in the player screen handler:
    // 5 = head, 6 = chest, 7 = legs, 8 = feet
    private static final int[] ARMOR_SLOTS = {5, 6, 7, 8};
    private static final EquipmentSlot[] EQUIPMENT_SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private int tickDelay = 0;

    public AutoArmor() {
        super("AutoArmor", "Automatically equips the best armor from your inventory.", Category.COMBAT);
    }

    @Override
    protected void onEnable() {
        tickDelay = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.interactionManager == null) return;

        // Don't run while in a container screen other than inventory
        if (mc.player.currentScreenHandler != mc.player.playerScreenHandler) return;

        // Small delay between equips to avoid issues
        if (tickDelay > 0) {
            tickDelay--;
            return;
        }

        for (int i = 0; i < 4; i++) {
            int armorScreenSlot = ARMOR_SLOTS[i];
            EquipmentSlot equipSlot = EQUIPMENT_SLOTS[i];

            ItemStack currentArmor = mc.player.currentScreenHandler.getSlot(armorScreenSlot).getStack();
            int currentProtection = getProtection(currentArmor);

            int bestSlot = -1;
            int bestProtection = currentProtection;

            // Search inventory (slots 9-44 in screen handler: 9-35 main, 36-44 hotbar)
            for (int slot = 9; slot <= 44; slot++) {
                ItemStack stack = mc.player.currentScreenHandler.getSlot(slot).getStack();
                if (stack.isEmpty()) continue;
                if (!(stack.getItem() instanceof ArmorItem armorItem)) continue;

                EquippableComponent equippable = stack.get(DataComponentTypes.EQUIPPABLE);
                if (equippable == null || equippable.slot() != equipSlot) continue;

                int protection = getProtection(stack);
                if (protection > bestProtection) {
                    bestProtection = protection;
                    bestSlot = slot;
                }
            }

            if (bestSlot != -1) {
                // Swap the armor: shift-click to equip, or pick up and swap
                if (currentArmor.isEmpty()) {
                    // Shift-click to equip
                    mc.interactionManager.clickSlot(
                        mc.player.currentScreenHandler.syncId,
                        bestSlot,
                        0,
                        SlotActionType.QUICK_MOVE,
                        mc.player
                    );
                } else {
                    // Pick up new armor
                    mc.interactionManager.clickSlot(
                        mc.player.currentScreenHandler.syncId,
                        bestSlot,
                        0,
                        SlotActionType.PICKUP,
                        mc.player
                    );
                    // Place in armor slot (swaps with current)
                    mc.interactionManager.clickSlot(
                        mc.player.currentScreenHandler.syncId,
                        armorScreenSlot,
                        0,
                        SlotActionType.PICKUP,
                        mc.player
                    );
                    // Put old armor where new one was
                    mc.interactionManager.clickSlot(
                        mc.player.currentScreenHandler.syncId,
                        bestSlot,
                        0,
                        SlotActionType.PICKUP,
                        mc.player
                    );
                }

                tickDelay = 2;
                return; // One swap per tick cycle
            }
        }
    }

    private int getProtection(ItemStack stack) {
        if (stack.isEmpty()) return -1;
        if (!(stack.getItem() instanceof ArmorItem armorItem)) return -1;
        // Lightweight score: prefer higher durability remaining.
        return stack.getMaxDamage() - stack.getDamage();
    }
}
