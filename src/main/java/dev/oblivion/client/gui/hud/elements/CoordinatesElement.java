package dev.oblivion.client.gui.hud.elements;

import dev.oblivion.client.gui.hud.HudElement;
import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import net.minecraft.client.gui.DrawContext;

public class CoordinatesElement extends HudElement {

    public CoordinatesElement() {
        super("Coordinates", 4, 24);
    }

    @Override
    public void render(DrawContext context) {
        if (mc.player == null) return;

        String overworld = String.format("XYZ  %.1f  %.1f  %.1f",
                mc.player.getX(), mc.player.getY(), mc.player.getZ());
        String nether = String.format("Nether  %.1f  %.1f  %.1f",
                mc.player.getX() / 8, mc.player.getY(), mc.player.getZ() / 8);

        int maxWidth = Math.max(mc.textRenderer.getWidth(overworld), mc.textRenderer.getWidth(nether));
        width = maxWidth + 12;
        height = 28;

        GuiRenderUtil.drawRoundedRect(context, x, y, width, height, 3, Theme.withAlpha(Theme.BG_PANEL, 160));

        context.drawText(mc.textRenderer, overworld, x + 4, y + 3, Theme.TEXT_PRIMARY, true);
        context.drawText(mc.textRenderer, nether, x + 4, y + 15, Theme.ACCENT_SECONDARY, true);
    }
}
