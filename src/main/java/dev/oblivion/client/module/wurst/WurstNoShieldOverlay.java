package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.item.Items;

public final class WurstNoShieldOverlay extends Module {
    public WurstNoShieldOverlay() {
        super("NoShieldOverlay", "Prevents shield raise animation/overlay.", Category.RENDER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        if (mc.player.getOffHandStack().getItem() != Items.SHIELD) return;
        if (mc.player.isUsingItem()) {
            mc.options.useKey.setPressed(false);
        }
    }
}
