package dev.oblivion.client.module.combat;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.EnumSetting;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class Offhand extends Module {

    public enum OffhandItem { TOTEM, CRYSTAL, SHIELD, GAP }

    private final EnumSetting<OffhandItem> item = settings.getDefaultGroup().add(
        new EnumSetting.Builder<OffhandItem>()
            .name("Item")
            .description("Item to keep in offhand.")
            .defaultValue(OffhandItem.TOTEM)
            .build()
    );

    private final DoubleSetting healthThreshold = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Health Threshold")
            .description("Switch to totem when health is below this value.")
            .defaultValue(10.0)
            .range(0.0, 36.0)
            .build()
    );

    public Offhand() {
        super("Offhand", "Manages items in your offhand slot.", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.interactionManager == null) return;

        net.minecraft.item.Item desired = switch (item.get()) {
            case TOTEM -> Items.TOTEM_OF_UNDYING;
            case CRYSTAL -> Items.END_CRYSTAL;
            case SHIELD -> Items.SHIELD;
            case GAP -> Items.ENCHANTED_GOLDEN_APPLE;
        };

        // Force totem if health is low
        if (mc.player.getHealth() <= healthThreshold.get() && item.get() != OffhandItem.TOTEM) {
            desired = Items.TOTEM_OF_UNDYING;
        }

        ItemStack offhand = mc.player.getOffHandStack();
        if (offhand.getItem() == desired) return;

        // Find the item in inventory
        int slot = findInInventory(desired);
        if (slot == -1) return;

        // Swap with offhand (slot 45)
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
    }

    private int findInInventory(net.minecraft.item.Item item) {
        for (int i = 0; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == item) {
                return i < 9 ? i + 36 : i;
            }
        }
        return -1;
    }
}
