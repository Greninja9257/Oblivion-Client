package dev.oblivion.client.gui.hud.elements;

import dev.oblivion.client.gui.hud.HudElement;
import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import net.minecraft.client.gui.DrawContext;

public class FpsElement extends HudElement {

    public FpsElement() {
        super("FPS", 4, 56);
    }

    @Override
    public void render(DrawContext context) {
        String fpsText = mc.getCurrentFps() + " FPS";
        width = mc.textRenderer.getWidth(fpsText) + 10;
        height = 14;

        GuiRenderUtil.drawRoundedRect(context, x, y, width, height, 3, Theme.withAlpha(Theme.BG_PANEL, 160));

        int fpsColor;
        int fps = mc.getCurrentFps();
        if (fps >= 60) fpsColor = Theme.ACCENT_ENABLED;
        else if (fps >= 30) fpsColor = Theme.NOTIFY_WARNING;
        else fpsColor = Theme.NOTIFY_DISABLED;

        context.drawText(mc.textRenderer, fpsText, x + 5, y + 3, fpsColor, true);
    }
}
