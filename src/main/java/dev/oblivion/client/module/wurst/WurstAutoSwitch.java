package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.EntityHitResult;

public final class WurstAutoSwitch extends Module {
    public WurstAutoSwitch() {
        super("AutoSwitch", "Switches to sword when targeting an entity.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        if (!(mc.crosshairTarget instanceof EntityHitResult)) return;

        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() instanceof SwordItem) {
                mc.player.getInventory().selectedSlot = i;
                return;
            }
        }
    }
}
