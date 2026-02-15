package dev.oblivion.client.gui.component.setting;

import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.client.gui.DrawContext;

public class SliderComponent extends SettingComponent {
    private final DoubleSetting doubleSetting;
    private boolean dragging = false;

    public SliderComponent(DoubleSetting setting, int x, int y, int width) {
        super(setting, x, y, width);
        this.doubleSetting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = isHovered(mouseX, mouseY);

        if (dragging) {
            updateValue(mouseX);
        }

        // Label
        String label = setting.name;
        String valueStr = String.format("%.2f", doubleSetting.get());
        context.drawText(mc.textRenderer, label, x + 4, y + 2, Theme.TEXT_SECONDARY, true);
        int valueWidth = mc.textRenderer.getWidth(valueStr);
        context.drawText(mc.textRenderer, valueStr, x + width - valueWidth - 4, y + 2, Theme.ACCENT_SECONDARY, true);

        // Slider track
        int trackX = x + 4;
        int trackY = y + height - 6;
        int trackW = width - 8;
        int trackH = 3;

        GuiRenderUtil.drawRoundedRect(context, trackX, trackY, trackW, trackH, 1, Theme.ACCENT_DISABLED);

        // Filled portion
        double ratio = (doubleSetting.get() - doubleSetting.min) / (doubleSetting.max - doubleSetting.min);
        int fillW = (int) (trackW * ratio);
        if (fillW > 0) {
            GuiRenderUtil.drawRoundedRect(context, trackX, trackY, fillW, trackH, 1, Theme.ACCENT_PRIMARY);
        }

        // Knob
        int knobX = trackX + fillW - 3;
        context.fill(knobX, trackY - 1, knobX + 6, trackY + trackH + 1, Theme.TEXT_PRIMARY);
    }

    private void updateValue(double mouseX) {
        int trackX = x + 4;
        int trackW = width - 8;
        double ratio = Math.max(0, Math.min(1, (mouseX - trackX) / trackW));
        double value = doubleSetting.min + (doubleSetting.max - doubleSetting.min) * ratio;
        doubleSetting.set(Math.round(value * 100.0) / 100.0);
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
        if (dragging) {
            dragging = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging) {
            updateValue(mouseX);
            return true;
        }
        return false;
    }
}
