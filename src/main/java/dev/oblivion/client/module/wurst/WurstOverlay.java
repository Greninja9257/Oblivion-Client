package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstOverlay extends Module {
    public WurstOverlay() {
        super("Overlay", "Draws a compact status overlay.", Category.RENDER);
    }

    @EventHandler
    public void onHud(RenderEvent.Hud event) {
        int x = 8;
        int y = 8;
        event.getContext().fill(x - 3, y - 3, x + 120, y + 28, 0x66000000);
        event.getContext().drawText(mc.textRenderer, "Wurst Overlay", x, y, 0xFF4DE2FF, true);
        event.getContext().drawText(mc.textRenderer, "TPS: N/A  Ping: N/A", x, y + 11, 0xFFDADADA, true);
    }
}
