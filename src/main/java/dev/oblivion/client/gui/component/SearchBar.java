package dev.oblivion.client.gui.component;

import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import net.minecraft.client.gui.DrawContext;

public class SearchBar extends Component {
    private String text = "";
    private boolean focused = false;
    private int cursorTick = 0;

    public SearchBar(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = Theme.SEARCH_BAR_HEIGHT;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = isHovered(mouseX, mouseY);
        int bgColor = focused ? Theme.BG_CARD_HOVER : Theme.BG_CARD;
        GuiRenderUtil.drawRoundedRect(context, x, y, width, height, 3, bgColor);

        if (focused) {
            GuiRenderUtil.drawOutline(context, x, y, width, height, Theme.ACCENT_PRIMARY);
        }

        String displayText = text.isEmpty() && !focused ? "Search modules..." : text;
        int textColor = text.isEmpty() && !focused ? Theme.TEXT_MUTED : Theme.TEXT_PRIMARY;
        context.drawText(mc.textRenderer, displayText, x + 6, y + (height - 8) / 2, textColor, true);

        if (focused) {
            cursorTick++;
            if (cursorTick % 20 < 10) {
                int cursorX = x + 6 + mc.textRenderer.getWidth(text);
                context.fill(cursorX, y + 4, cursorX + 1, y + height - 4, Theme.TEXT_PRIMARY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        focused = isHovered((int) mouseX, (int) mouseY);
        return focused;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focused) return false;
        if (keyCode == 259 && !text.isEmpty()) { // BACKSPACE
            text = text.substring(0, text.length() - 1);
            return true;
        }
        if (keyCode == 256) { // ESCAPE
            focused = false;
            text = "";
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!focused) return false;
        if (chr >= 32 && text.length() < 32) {
            text += chr;
            return true;
        }
        return false;
    }

    public String getText() { return text; }
    public boolean isFocused() { return focused; }
    public void setFocused(boolean focused) { this.focused = focused; }
}
