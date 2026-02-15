package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstPortalGui extends Module {
    public WurstPortalGui() {
        super("PortalGui", "Keeps GUI responsive while standing in portals.", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        // Keep sneak/use states stable while a GUI is open in portal contexts.
        if (mc.currentScreen != null && mc.player.isSubmergedInWater()) {
            mc.options.sneakKey.setPressed(false);
        }
    }
}
