package dev.oblivion.client.util;

public class ColorUtil {
    public static int rgba(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int rgb(int r, int g, int b) {
        return rgba(r, g, b, 255);
    }

    public static int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    public static int rainbow(float offset) {
        float hue = (System.currentTimeMillis() % 3000) / 3000f + offset;
        return java.awt.Color.HSBtoRGB(hue, 0.8f, 1.0f) | 0xFF000000;
    }

    public static int categoryColor(dev.oblivion.client.module.Category category) {
        return switch (category) {
            case COMBAT -> rgb(255, 80, 80);
            case MOVEMENT -> rgb(80, 180, 255);
            case RENDER -> rgb(255, 200, 80);
            case PLAYER -> rgb(80, 255, 120);
            case WORLD -> rgb(200, 130, 255);
            case MISC -> rgb(180, 180, 180);
            case BOTS -> rgb(80, 255, 230);
        };
    }
}
