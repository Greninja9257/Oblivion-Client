package dev.oblivion.client.gui.component.setting;

import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import dev.oblivion.client.setting.impl.EnumSetting;
import net.minecraft.client.gui.DrawContext;

public class EnumSettingComponent extends SettingComponent {
    private final EnumSetting<?> enumSetting;

    public EnumSettingComponent(EnumSetting<?> setting, int x, int y, int width) {
        super(setting, x, y, width);
        this.enumSetting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = isHovered(mouseX, mouseY);

        context.drawText(mc.textRenderer, setting.name, x + 4, y + (height - 8) / 2, Theme.TEXT_SECONDARY, true);

        String valueName = enumSetting.get().name();
        int btnW = Math.max(mc.textRenderer.getWidth(valueName) + 12, 50);
        int btnX = x + width - btnW - 4;
        int btnY = y + 3;
        int btnH = height - 6;

        int btnColor = hovered ? Theme.BG_CARD_HOVER : Theme.BG_CARD;
        GuiRenderUtil.drawRoundedRect(context, btnX, btnY, btnW, btnH, 3, btnColor);
        GuiRenderUtil.drawOutline(context, btnX, btnY, btnW, btnH, Theme.ACCENT_PRIMARY);

        int textX = btnX + (btnW - mc.textRenderer.getWidth(valueName)) / 2;
        context.drawText(mc.textRenderer, valueName, textX, btnY + (btnH - 8) / 2, Theme.TEXT_ACCENT, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered((int) mouseX, (int) mouseY)) {
            enumSetting.cycle();
            return true;
        }
        return false;
    }
}
