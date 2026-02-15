package dev.oblivion.client.gui.component.setting;

import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import dev.oblivion.client.setting.impl.ColorSetting;
import net.minecraft.client.gui.DrawContext;

public class ColorPickerComponent extends SettingComponent {
    private final ColorSetting colorSetting;

    public ColorPickerComponent(ColorSetting setting, int x, int y, int width) {
        super(setting, x, y, width);
        this.colorSetting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = isHovered(mouseX, mouseY);

        context.drawText(mc.textRenderer, setting.name, x + 4, y + (height - 8) / 2, Theme.TEXT_SECONDARY, true);

        // Color preview swatch
        int swatchSize = height - 8;
        int swatchX = x + width - swatchSize - 4;
        int swatchY = y + 4;

        // Checkerboard background for alpha
        for (int i = 0; i < swatchSize; i += 4) {
            for (int j = 0; j < swatchSize; j += 4) {
                int checkerColor = ((i + j) / 4) % 2 == 0 ? 0xFFCCCCCC : 0xFF999999;
                context.fill(swatchX + i, swatchY + j, swatchX + i + 4, swatchY + j + 4, checkerColor);
            }
        }
        context.fill(swatchX, swatchY, swatchX + swatchSize, swatchY + swatchSize, colorSetting.get());
        GuiRenderUtil.drawOutline(context, swatchX, swatchY, swatchSize, swatchSize, Theme.TEXT_MUTED);

        // Hex value
        String hex = "#" + String.format("%08X", colorSetting.get());
        int hexWidth = mc.textRenderer.getWidth(hex);
        context.drawText(mc.textRenderer, hex, swatchX - hexWidth - 4, y + (height - 8) / 2, Theme.TEXT_MUTED, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered((int) mouseX, (int) mouseY)) {
            // Cycle through preset colors on click
            int[] presets = {
                0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFFFF00,
                0xFFFF00FF, 0xFF00FFFF, 0xFFFFFFFF, 0xFFFF8800
            };
            int current = colorSetting.get();
            for (int i = 0; i < presets.length; i++) {
                if (presets[i] == current) {
                    colorSetting.set(presets[(i + 1) % presets.length]);
                    return true;
                }
            }
            colorSetting.set(presets[0]);
            return true;
        }
        return false;
    }
}
