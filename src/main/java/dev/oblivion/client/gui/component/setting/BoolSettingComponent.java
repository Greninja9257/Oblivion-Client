package dev.oblivion.client.gui.component.setting;

import dev.oblivion.client.gui.animation.Animation;
import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import dev.oblivion.client.setting.impl.BoolSetting;
import net.minecraft.client.gui.DrawContext;

public class BoolSettingComponent extends SettingComponent {
    private final BoolSetting boolSetting;
    private final Animation toggleAnim;

    public BoolSettingComponent(BoolSetting setting, int x, int y, int width) {
        super(setting, x, y, width);
        this.boolSetting = setting;
        this.toggleAnim = new Animation(setting.get() ? 1f : 0f, Theme.ANIM_SPEED_FAST);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        toggleAnim.setTarget(boolSetting.get() ? 1f : 0f);
        toggleAnim.update();

        hovered = isHovered(mouseX, mouseY);

        // Label
        context.drawText(mc.textRenderer, setting.name, x + 4, y + (height - 8) / 2, Theme.TEXT_SECONDARY, true);

        // Toggle switch
        int toggleW = 24;
        int toggleH = 12;
        int toggleX = x + width - toggleW - 4;
        int toggleY = y + (height - toggleH) / 2;

        float t = toggleAnim.get();
        int trackColor = Theme.lerpColor(Theme.ACCENT_DISABLED, Theme.ACCENT_ENABLED, t);
        GuiRenderUtil.drawRoundedRect(context, toggleX, toggleY, toggleW, toggleH, 6, trackColor);

        // Knob
        int knobSize = toggleH - 4;
        int knobX = (int) (toggleX + 2 + (toggleW - knobSize - 4) * t);
        int knobY = toggleY + 2;
        GuiRenderUtil.drawRoundedRect(context, knobX, knobY, knobSize, knobSize, 4, Theme.TEXT_PRIMARY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isHovered((int) mouseX, (int) mouseY)) {
            boolSetting.set(!boolSetting.get());
            return true;
        }
        return false;
    }
}
