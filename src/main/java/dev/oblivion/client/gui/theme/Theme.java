package dev.oblivion.client.gui.theme;

public class Theme {
    // Background colors
    public static final int BG_PRIMARY = 0xF0080810;
    public static final int BG_SECONDARY = 0xF0101020;
    public static final int BG_PANEL = 0xE8141428;
    public static final int BG_CARD = 0xE01A1A30;
    public static final int BG_CARD_HOVER = 0xE0222240;
    public static final int BG_HEADER = 0xF00C0C1A;

    // Accent colors (cyberpunk glow)
    public static final int ACCENT_PRIMARY = 0xFF8B5CF6;    // Purple
    public static final int ACCENT_SECONDARY = 0xFF06B6D4;  // Cyan
    public static final int ACCENT_TERTIARY = 0xFFE040FB;   // Pink/Magenta
    public static final int ACCENT_ENABLED = 0xFF00E676;    // Neon green
    public static final int ACCENT_DISABLED = 0xFF4A4A5A;   // Dark gray

    // Text colors
    public static final int TEXT_PRIMARY = 0xFFE2E8F0;
    public static final int TEXT_SECONDARY = 0xFF94A3B8;
    public static final int TEXT_MUTED = 0xFF64748B;
    public static final int TEXT_ACCENT = 0xFF8B5CF6;
    public static final int TEXT_ENABLED = 0xFF00E676;

    // Notification colors
    public static final int NOTIFY_ENABLED = 0xFF00E676;
    public static final int NOTIFY_DISABLED = 0xFFFF5252;
    public static final int NOTIFY_INFO = 0xFF06B6D4;
    public static final int NOTIFY_WARNING = 0xFFFFAB40;

    // GUI dimensions
    public static final int PADDING = 8;
    public static final int PADDING_SMALL = 4;
    public static final int PADDING_LARGE = 12;
    public static final int HEADER_HEIGHT = 40;
    public static final int TAB_HEIGHT = 28;
    public static final int MODULE_CARD_HEIGHT = 32;
    public static final int SETTING_ROW_HEIGHT = 22;
    public static final int SEARCH_BAR_HEIGHT = 24;
    public static final int SCROLLBAR_WIDTH = 3;

    // Animation
    public static final float ANIM_SPEED_FAST = 0.2f;
    public static final float ANIM_SPEED_NORMAL = 0.12f;
    public static final float ANIM_SPEED_SLOW = 0.06f;

    // Glow
    public static final int GLOW_PRIMARY = 0x408B5CF6;
    public static final int GLOW_CYAN = 0x4006B6D4;
    public static final int GLOW_ENABLED = 0x3000E676;

    public static int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    public static int lerpColor(int from, int to, float t) {
        t = Math.max(0, Math.min(1, t));
        int fa = (from >> 24) & 0xFF, fr = (from >> 16) & 0xFF, fg = (from >> 8) & 0xFF, fb = from & 0xFF;
        int ta = (to >> 24) & 0xFF, tr = (to >> 16) & 0xFF, tg = (to >> 8) & 0xFF, tb = to & 0xFF;
        int a = (int) (fa + (ta - fa) * t);
        int r = (int) (fr + (tr - fr) * t);
        int g = (int) (fg + (tg - fg) * t);
        int b = (int) (fb + (tb - fb) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
