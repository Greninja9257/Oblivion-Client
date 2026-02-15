package dev.oblivion.client.gui.component.setting;

import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.client.gui.DrawContext;

public class IntSliderComponent extends SettingComponent {
    private final IntSetting intSetting;
    private boolean dragging = false;

    public IntSliderComponent(IntSetting setting, int x, int y, int width) {
        super(setting, x, y, width);
        this.intSetting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = isHovered(mouseX, mouseY);

        if (dragging) {
            updateValue(mouseX);
        }

        // Label
        context.drawText(mc.textRenderer, setting.name, x + 4, y + 2, Theme.TEXT_SECONDARY, true);
        String valueStr = String.valueOf(intSetting.get());
        int valueWidth = mc.textRenderer.getWidth(valueStr);
        context.drawText(mc.textRenderer, valueStr, x + width - valueWidth - 4, y + 2, Theme.ACCENT_SECONDARY, true);

        // Slider track
        int trackX = x + 4;
        int trackY = y + height - 6;
        int trackW = width - 8;
        int trackH = 3;

        GuiRenderUtil.drawRoundedRect(context, trackX, trackY, trackW, trackH, 1, Theme.ACCENT_DISABLED);

        double ratio = (double) (intSetting.get() - intSetting.min) / (intSetting.max - intSetting.min);
        int fillW = (int) (trackW * ratio);
        if (fillW > 0) {
            GuiRenderUtil.drawRoundedRect(context, trackX, trackY, fillW, trackH, 1, Theme.ACCENT_PRIMARY);
        }

        int knobX = trackX + fillW - 3;
        context.fill(knobX, trackY - 1, knobX + 6, trackY + trackH + 1, Theme.TEXT_PRIMARY);
    }

    private void updateValue(double mouseX) {
        int trackX = x + 4;
        int trackW = width - 8;
        double ratio = Math.max(0, Math.min(1, (mouseX - trackX) / trackW));
        int value = (int) Math.round(intSetting.min + (intSetting.max - intSetting.min) * ratio);
        intSetting.set(value);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isHovered((int) mouseX, (int) mouseY)) {
            dragging = true;
            updateValue(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging) { dragging = false; return true; }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging) { updateValue(mouseX); return true; }
        return false;
    }
}
