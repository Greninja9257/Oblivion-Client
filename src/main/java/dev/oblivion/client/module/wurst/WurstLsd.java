package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstLsd extends Module {
    private int tick;

    public WurstLsd() {
        super("Lsd", "Applies chaotic HUD color cycling overlay.", Category.RENDER);
    }

    @EventHandler
    public void onHud(RenderEvent.Hud event) {
        tick++;
        int r = (int) (128 + 127 * Math.sin(tick * 0.1));
        int g = (int) (128 + 127 * Math.sin(tick * 0.13 + 2));
        int b = (int) (128 + 127 * Math.sin(tick * 0.17 + 4));
        int color = (0x66 << 24) | (r << 16) | (g << 8) | b;

        int w = event.getContext().getScaledWindowWidth();
        int h = event.getContext().getScaledWindowHeight();
        event.getContext().fill(0, 0, w, h, color);
    }
}
