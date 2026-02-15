package dev.oblivion.client.gui.notification;

import dev.oblivion.client.gui.animation.Animation;
import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import dev.oblivion.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NotificationManager {
    private final List<Notification> notifications = new ArrayList<>();
    private static final int MAX_VISIBLE = 5;
    private static final int NOTIFICATION_WIDTH = 180;
    private static final int NOTIFICATION_HEIGHT = 26;
    private static final int SPACING = 4;
    private static final int DURATION_TICKS = 60;
    private static final int FADE_TICKS = 10;

    public void show(String message, Module.NotificationType type) {
        notifications.add(0, new Notification(message, type, DURATION_TICKS));
        if (notifications.size() > MAX_VISIBLE + 2) {
            notifications.subList(MAX_VISIBLE + 2, notifications.size()).clear();
        }
    }

    public void render(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.getWindow() == null) return;

        int screenW = mc.getWindow().getScaledWidth();
        int screenH = mc.getWindow().getScaledHeight();

        Iterator<Notification> it = notifications.iterator();
        int index = 0;
        while (it.hasNext()) {
            Notification n = it.next();
            n.ticksAlive++;
            n.slideAnim.update();

            if (n.ticksAlive > n.duration + FADE_TICKS) {
                it.remove();
                continue;
            }

            if (index >= MAX_VISIBLE) {
                it.remove();
                continue;
            }

            float slide = n.slideAnim.get();
            float fadeAlpha = 1.0f;
            if (n.ticksAlive > n.duration) {
                fadeAlpha = 1.0f - (float) (n.ticksAlive - n.duration) / FADE_TICKS;
            }

            int targetY = screenH - 40 - (index * (NOTIFICATION_HEIGHT + SPACING));
            int x = (int) (screenW - NOTIFICATION_WIDTH * slide);
            int y = targetY;

            int bgAlpha = (int) (200 * fadeAlpha);
            int textAlpha = (int) (255 * fadeAlpha);

            int bgColor = Theme.withAlpha(Theme.BG_PANEL, bgAlpha);
            int accentColor = getAccentColor(n.type);
            int accentWithAlpha = Theme.withAlpha(accentColor, (int) (255 * fadeAlpha));

            // Background
            GuiRenderUtil.drawRoundedRect(context, x, y, NOTIFICATION_WIDTH, NOTIFICATION_HEIGHT, 3, bgColor);

            // Accent bar on left
            context.fill(x, y + 2, x + 3, y + NOTIFICATION_HEIGHT - 2, accentWithAlpha);

            // Text
            int textColor = Theme.withAlpha(Theme.TEXT_PRIMARY, textAlpha);
            context.drawText(mc.textRenderer, n.message, x + 8, y + (NOTIFICATION_HEIGHT - 8) / 2, textColor, true);

            index++;
        }
    }

    private int getAccentColor(Module.NotificationType type) {
        return switch (type) {
            case ENABLED -> Theme.NOTIFY_ENABLED;
            case DISABLED -> Theme.NOTIFY_DISABLED;
            case INFO -> Theme.NOTIFY_INFO;
        };
    }

    private static class Notification {
        final String message;
        final Module.NotificationType type;
        final int duration;
        int ticksAlive = 0;
        final Animation slideAnim;

        Notification(String message, Module.NotificationType type, int duration) {
            this.message = message;
            this.type = type;
            this.duration = duration;
            this.slideAnim = new Animation(0f, Theme.ANIM_SPEED_FAST);
            this.slideAnim.setTarget(1f);
        }
    }
}
