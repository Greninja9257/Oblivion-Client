package dev.oblivion.client.gui.component.setting;

import dev.oblivion.client.gui.theme.Theme;
import dev.oblivion.client.setting.Setting;
import net.minecraft.client.gui.DrawContext;

public class GenericSettingComponent extends SettingComponent {

    public GenericSettingComponent(Setting<?> setting, int x, int y, int width) {
        super(setting, x, y, width);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawText(mc.textRenderer, setting.name + ": " + setting.get(), x + 4, y + (height - 8) / 2, Theme.TEXT_MUTED, true);
    }
}
