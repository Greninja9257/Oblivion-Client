package dev.oblivion.client.gui.component.setting;

import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import dev.oblivion.client.setting.impl.StringSetting;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

public class StringSettingComponent extends SettingComponent {
    private final StringSetting stringSetting;
    private boolean focused = false;
    private int cursorTick = 0;
    private String draftValue;

    public StringSettingComponent(StringSetting setting, int x, int y, int width) {
        super(setting, x, y, width);
        this.stringSetting = setting;
        this.draftValue = setting.get();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = isHovered(mouseX, mouseY);
        context.drawText(mc.textRenderer, stringSetting.name, x + 4, y + (height - 8) / 2, Theme.TEXT_SECONDARY, true);

        String display = focused ? draftValue : stringSetting.get();
        if (display == null) display = "";

        int inputWidth = Math.min(160, Math.max(100, width / 2));
        int inputX = x + width - inputWidth - 4;
        int inputY = y + 3;
        int inputH = height - 6;
        int inputBg = focused ? Theme.BG_CARD_HOVER : Theme.BG_SECONDARY;
        GuiRenderUtil.drawRoundedRect(context, inputX, inputY, inputWidth, inputH, 3, inputBg);
        if (focused) {
            GuiRenderUtil.drawOutline(context, inputX, inputY, inputWidth, inputH, Theme.ACCENT_PRIMARY);
        }

        int valueColor = display.isEmpty() ? Theme.TEXT_MUTED : Theme.TEXT_PRIMARY;
        int maxTextWidth = inputWidth - 10;
        String clipped = clipToWidth(display, maxTextWidth);
        context.drawText(mc.textRenderer, clipped, inputX + 5, y + (height - 8) / 2, valueColor, true);

        if (focused) {
            cursorTick++;
            if (cursorTick % 20 < 10) {
                int cursorX = inputX + 5 + mc.textRenderer.getWidth(clipped);
                if (cursorX < inputX + inputWidth - 4) {
                    context.fill(cursorX, y + 5, cursorX + 1, y + height - 5, Theme.TEXT_PRIMARY);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean nowFocused = isHovered((int) mouseX, (int) mouseY);
        if (focused && !nowFocused) {
            commit();
        }
        focused = nowFocused;
        if (focused) {
            draftValue = stringSetting.get();
            if (draftValue == null) draftValue = "";
        }
        return nowFocused;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focused) return false;

        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (!draftValue.isEmpty()) {
                draftValue = draftValue.substring(0, draftValue.length() - 1);
            }
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            commit();
            focused = false;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            draftValue = stringSetting.get();
            if (draftValue == null) draftValue = "";
            focused = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!focused) return false;
        if (chr >= 32 && draftValue.length() < 64) {
            draftValue += chr;
            return true;
        }
        return false;
    }

    private void commit() {
        stringSetting.set(draftValue == null ? "" : draftValue);
    }

    @Override
    public boolean isTextInputFocused() {
        return focused;
    }

    private String clipToWidth(String value, int maxWidth) {
        if (mc.textRenderer.getWidth(value) <= maxWidth) return value;
        String suffix = "...";
        int suffixWidth = mc.textRenderer.getWidth(suffix);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (mc.textRenderer.getWidth(out.toString() + c) + suffixWidth > maxWidth) break;
            out.append(c);
        }
        return out + suffix;
    }
}
