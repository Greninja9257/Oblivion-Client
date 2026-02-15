package dev.oblivion.client.module.player;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.DoubleSetting;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public class AutoSoup extends Module {

    private final DoubleSetting healthThreshold = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Health Threshold")
            .description("Use soup when total health is at or below this value.")
            .defaultValue(14.0)
            .range(1.0, 36.0)
            .build()
    );

    private final IntSetting useDelay = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Use Delay")
            .description("Ticks between soup uses.")
            .defaultValue(4)
            .range(0, 20)
            .build()
    );

    private final BoolSetting refillHotbar = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Refill Hotbar")
            .description("Move soup from inventory into hotbar when needed.")
            .defaultValue(true)
            .build()
    );

    private final IntSetting refillSlot = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Refill Slot")
            .description("Preferred hotbar slot for soup refill (1-9).")
            .defaultValue(8)
            .range(1, 9)
            .visible(refillHotbar::get)
            .build()
    );

    private int delayTicks;

    public AutoSoup() {
        super("AutoSoup", "Automatically uses mushroom soup on low health.", Category.PLAYER);
    }

    @Override
    protected void onEnable() {
        delayTicks = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (mc.player.getAbilities().creativeMode) return;
        if (mc.player.isUsingItem()) return;

        if (delayTicks > 0) {
            delayTicks--;
            return;
        }

        double health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        if (health > healthThreshold.get()) return;

        int soupHotbarSlot = findSoupInHotbar();
        if (soupHotbarSlot == -1 && refillHotbar.get()) {
            refillSoupToHotbar();
            soupHotbarSlot = findSoupInHotbar();
        }

        if (soupHotbarSlot == -1) return;

        int previousSlot = mc.player.getInventory().selectedSlot;
        mc.player.getInventory().selectedSlot = soupHotbarSlot;

        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        mc.player.swingHand(Hand.MAIN_HAND);

        mc.player.getInventory().selectedSlot = previousSlot;
        delayTicks = useDelay.get();
    }

    private int findSoupInHotbar() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.MUSHROOM_STEW) {
                return i;
            }
        }
        return -1;
    }

    private int findSoupInInventory() {
        for (int i = 9; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.MUSHROOM_STEW) {
                return i;
            }
        }
        return -1;
    }

    private void refillSoupToHotbar() {
        int inventorySlot = findSoupInInventory();
        if (inventorySlot == -1) return;

        int preferredHotbarSlot = refillSlot.get() - 1;
        int targetHotbarSlot = preferredHotbarSlot;

        // If preferred slot is occupied by non-soup, use the first empty slot instead.
        if (!mc.player.getInventory().getStack(preferredHotbarSlot).isEmpty()
            && mc.player.getInventory().getStack(preferredHotbarSlot).getItem() != Items.MUSHROOM_STEW) {
            targetHotbarSlot = findEmptyHotbarSlot();
            if (targetHotbarSlot == -1) return;
        }

        int syncId = mc.player.currentScreenHandler.syncId;
        int from = inventorySlot;
        int to = 36 + targetHotbarSlot;

        mc.interactionManager.clickSlot(syncId, from, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(syncId, to, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(syncId, from, 0, SlotActionType.PICKUP, mc.player);
    }

    private int findEmptyHotbarSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }
}
