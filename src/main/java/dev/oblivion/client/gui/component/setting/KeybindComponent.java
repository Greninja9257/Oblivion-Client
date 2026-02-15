package dev.oblivion.client.gui.component.setting;

import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import dev.oblivion.client.setting.impl.KeybindSetting;
import dev.oblivion.client.util.KeyUtil;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

public class KeybindComponent extends SettingComponent {
    private final KeybindSetting keybindSetting;
    private boolean listening = false;

    public KeybindComponent(KeybindSetting setting, int x, int y, int width) {
        super(setting, x, y, width);
        this.keybindSetting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = isHovered(mouseX, mouseY);

        context.drawText(mc.textRenderer, setting.name, x + 4, y + (height - 8) / 2, Theme.TEXT_SECONDARY, true);

        String keyText = listening ? "Press a key..." : KeyUtil.getKeyName(keybindSetting.get());
        int btnW = Math.max(mc.textRenderer.getWidth(keyText) + 12, 60);
        int btnX = x + width - btnW - 4;
        int btnY = y + 3;
        int btnH = height - 6;

        int btnColor = listening ? Theme.ACCENT_PRIMARY : (hovered ? Theme.BG_CARD_HOVER : Theme.BG_CARD);
        GuiRenderUtil.drawRoundedRect(context, btnX, btnY, btnW, btnH, 3, btnColor);
        if (!listening) {
            GuiRenderUtil.drawOutline(context, btnX, btnY, btnW, btnH, Theme.ACCENT_DISABLED);
        }

        int textColor = listening ? Theme.BG_PRIMARY : Theme.TEXT_PRIMARY;
        int textX = btnX + (btnW - mc.textRenderer.getWidth(keyText)) / 2;
        context.drawText(mc.textRenderer, keyText, textX, btnY + (btnH - 8) / 2, textColor, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered((int) mouseX, (int) mouseY)) {
            listening = !listening;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!listening) return false;
        listening = false;
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            keybindSetting.set(GLFW.GLFW_KEY_UNKNOWN);
        } else {
            keybindSetting.set(keyCode);
        }
        return true;
    }

    public boolean isListening() { return listening; }
}
