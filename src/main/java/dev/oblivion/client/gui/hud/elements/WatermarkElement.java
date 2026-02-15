package dev.oblivion.client.gui.hud.elements;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.gui.hud.HudElement;
import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import net.minecraft.client.gui.DrawContext;

public class WatermarkElement extends HudElement {

    public WatermarkElement() {
        super("Watermark", 4, 4);
    }

    @Override
    public void render(DrawContext context) {
        String text = "OBLIVION v" + OblivionClient.VERSION;
        width = mc.textRenderer.getWidth(text) + 12;
        height = 16;

        // Background with glow
        GuiRenderUtil.drawRoundedRect(context, x, y, width, height, 3, Theme.BG_PANEL);
        context.fill(x, y + height - 2, x + width, y + height, Theme.ACCENT_PRIMARY);

        // Text with glow effect
        context.drawText(mc.textRenderer, text, x + 6, y + 4, Theme.ACCENT_PRIMARY, true);
    }
}
