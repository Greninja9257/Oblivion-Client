package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstFancyChat extends Module {
    public WurstFancyChat() {
        super("FancyChat", "Displays decorative chat mode indicator.", Category.MISC);
    }

    @EventHandler
    public void onHud(RenderEvent.Hud event) {
        String label = "FancyChat ✦ ON";
        int w = mc.textRenderer.getWidth(label);
        int x = event.getContext().getScaledWindowWidth() - w - 8;
        int y = event.getContext().getScaledWindowHeight() - 30;
        event.getContext().drawText(mc.textRenderer, label, x, y, 0xFFFF75D8, true);
    }
}
