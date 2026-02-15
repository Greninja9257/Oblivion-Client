package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstRainbowUi extends Module {
    private int tick;

    public WurstRainbowUi() {
        super("RainbowUi", "Draws rainbow header text on HUD.", Category.RENDER);
    }

    @EventHandler
    public void onHud(RenderEvent.Hud event) {
        tick++;
        int r = (int) (128 + 127 * Math.sin(tick * 0.08));
        int g = (int) (128 + 127 * Math.sin(tick * 0.08 + 2));
        int b = (int) (128 + 127 * Math.sin(tick * 0.08 + 4));
        int color = 0xFF000000 | (r << 16) | (g << 8) | b;

        event.getContext().drawText(mc.textRenderer, "RainbowUI", 8, 8, color, true);
    }
}
