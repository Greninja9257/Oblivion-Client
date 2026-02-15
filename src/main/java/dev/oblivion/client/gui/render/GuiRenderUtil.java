package dev.oblivion.client.gui.render;

import net.minecraft.client.gui.DrawContext;

public class GuiRenderUtil {

    public static void drawRect(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + h, color);
    }

    public static void drawRoundedRect(DrawContext ctx, int x, int y, int w, int h, int radius, int color) {
        // Approximate rounded rect with filled rects
        ctx.fill(x + radius, y, x + w - radius, y + h, color);
        ctx.fill(x, y + radius, x + radius, y + h - radius, color);
        ctx.fill(x + w - radius, y + radius, x + w, y + h - radius, color);
        // Corner fills (small squares to approximate rounding)
        ctx.fill(x + 1, y + 1, x + radius, y + radius, color);
        ctx.fill(x + w - radius, y + 1, x + w - 1, y + radius, color);
        ctx.fill(x + 1, y + h - radius, x + radius, y + h - 1, color);
        ctx.fill(x + w - radius, y + h - radius, x + w - 1, y + h - 1, color);
    }

    public static void drawGlowRect(DrawContext ctx, int x, int y, int w, int h, int glowColor, int layers) {
        for (int i = layers; i >= 1; i--) {
            int alpha = (((glowColor >> 24) & 0xFF) / (i + 1));
            int color = (glowColor & 0x00FFFFFF) | (alpha << 24);
            ctx.fill(x - i, y - i, x + w + i, y + h + i, color);
        }
    }

    public static void drawGradientRect(DrawContext ctx, int x, int y, int w, int h, int colorTop, int colorBottom) {
        ctx.fillGradient(x, y, x + w, y + h, colorTop, colorBottom);
    }

    public static void drawHLine(DrawContext ctx, int x, int y, int width, int color) {
        ctx.fill(x, y, x + width, y + 1, color);
    }

    public static void drawVLine(DrawContext ctx, int x, int y, int height, int color) {
        ctx.fill(x, y, x + 1, y + height, color);
    }

    public static void drawOutline(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + 1, color);
        ctx.fill(x, y + h - 1, x + w, y + h, color);
        ctx.fill(x, y, x + 1, y + h, color);
        ctx.fill(x + w - 1, y, x + w, y + h, color);
    }

    public static void drawShadow(DrawContext ctx, int x, int y, int w, int h) {
        int shadowColor1 = 0x40000000;
        int shadowColor2 = 0x20000000;
        int shadowColor3 = 0x10000000;
        ctx.fill(x + 2, y + h, x + w + 2, y + h + 1, shadowColor1);
        ctx.fill(x + w, y + 2, x + w + 1, y + h, shadowColor1);
        ctx.fill(x + 3, y + h + 1, x + w + 3, y + h + 2, shadowColor2);
        ctx.fill(x + w + 1, y + 3, x + w + 2, y + h + 1, shadowColor2);
        ctx.fill(x + 4, y + h + 2, x + w + 4, y + h + 3, shadowColor3);
        ctx.fill(x + w + 2, y + 4, x + w + 3, y + h + 2, shadowColor3);
    }
}
