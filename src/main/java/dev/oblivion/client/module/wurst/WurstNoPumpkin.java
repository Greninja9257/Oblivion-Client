package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public final class WurstNoPumpkin extends Module {
    public WurstNoPumpkin() {
        super("NoPumpkin", "Automatically removes carved pumpkin from helmet slot.", Category.RENDER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.interactionManager == null) return;

        int helmetSlot = 5; // Screen handler helmet slot index.
        if (mc.player.currentScreenHandler.getSlot(helmetSlot).getStack().getItem() != Items.CARVED_PUMPKIN) return;

        int emptyInvSlot = findEmptyInventorySlot();
        if (emptyInvSlot == -1) return;

        int syncId = mc.player.currentScreenHandler.syncId;
        mc.interactionManager.clickSlot(syncId, helmetSlot, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(syncId, emptyInvSlot, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(syncId, helmetSlot, 0, SlotActionType.PICKUP, mc.player);
    }

    private int findEmptyInventorySlot() {
        for (int i = 9; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).isEmpty()) return i;
        }
        return -1;
    }
}
