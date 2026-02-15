package dev.oblivion.client.gui.hud.elements;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.gui.hud.HudElement;
import dev.oblivion.client.gui.theme.Theme;
import dev.oblivion.client.module.Module;
import net.minecraft.client.gui.DrawContext;

import java.util.Comparator;
import java.util.List;

public class ActiveModulesElement extends HudElement {

    public ActiveModulesElement() {
        super("ActiveModules", -1, 2);  // -1 means right-aligned
    }

    @Override
    public void render(DrawContext context) {
        List<Module> enabled = OblivionClient.get().moduleManager.getEnabled().stream()
                .filter(Module::isDrawn)
                .sorted(Comparator.comparingInt((Module m) -> mc.textRenderer.getWidth(m.name)).reversed())
                .toList();

        if (enabled.isEmpty()) {
            width = 0;
            height = 0;
            return;
        }

        int screenW = mc.getWindow().getScaledWidth();
        int lineHeight = 12;
        int maxWidth = 0;

        for (int i = 0; i < enabled.size(); i++) {
            Module m = enabled.get(i);
            int textWidth = mc.textRenderer.getWidth(m.name);
            maxWidth = Math.max(maxWidth, textWidth);

            int drawX;
            if (x < 0) {
                drawX = screenW - textWidth - 4;
            } else {
                drawX = x;
            }
            int drawY = y + i * lineHeight;

            // Background per module
            int bgX = x < 0 ? screenW - textWidth - 8 : x;
            context.fill(bgX, drawY, bgX + textWidth + 8, drawY + lineHeight, Theme.withAlpha(Theme.BG_PANEL, 160));

            // Accent bar
            if (x < 0) {
                context.fill(screenW - 2, drawY, screenW, drawY + lineHeight, Theme.ACCENT_PRIMARY);
            } else {
                context.fill(x, drawY, x + 2, drawY + lineHeight, Theme.ACCENT_PRIMARY);
            }

            // Module name with category-based color cycling
            int hue = (i * 35) % 360;
            int color = hueToColor(hue);
            context.drawText(mc.textRenderer, m.name, drawX, drawY + 2, color, true);
        }

        width = maxWidth + 8;
        height = enabled.size() * lineHeight;
    }

    private int hueToColor(int hue) {
        float h = hue / 360f;
        float s = 0.7f;
        float v = 1.0f;

        int hi = (int) (h * 6) % 6;
        float f = h * 6 - hi;
        float p = v * (1 - s);
        float q = v * (1 - f * s);
        float t = v * (1 - (1 - f) * s);

        float r, g, b;
        switch (hi) {
            case 0 -> { r = v; g = t; b = p; }
            case 1 -> { r = q; g = v; b = p; }
            case 2 -> { r = p; g = v; b = t; }
            case 3 -> { r = p; g = q; b = v; }
            case 4 -> { r = t; g = p; b = v; }
            default -> { r = v; g = p; b = q; }
        }

        return 0xFF000000 | ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
    }
}
