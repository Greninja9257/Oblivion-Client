package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.text.Text;

public final class WurstNoOverlay extends Module {
    public WurstNoOverlay() {
        super("NoOverlay", "Clears overlay messages and titles.", Category.RENDER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.inGameHud == null) return;
        mc.inGameHud.setOverlayMessage(Text.empty(), false);
        mc.inGameHud.clearTitle();
    }
}
