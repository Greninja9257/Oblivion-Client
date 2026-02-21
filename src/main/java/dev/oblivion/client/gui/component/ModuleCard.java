package dev.oblivion.client.gui.component;

import dev.oblivion.client.gui.animation.Animation;
import dev.oblivion.client.gui.component.setting.SettingComponent;
import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.Setting;
import dev.oblivion.client.util.KeyUtil;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class ModuleCard extends Component {
    private final Module module;
    private boolean expanded = false;
    private final Animation expandAnim;
    private final Animation toggleAnim;
    private final Animation hoverAnim;
    private final List<SettingComponent> settingComponents = new ArrayList<>();
    private boolean bindListening = false;

    private static final int COLLAPSED_HEIGHT = Theme.MODULE_CARD_HEIGHT;

    public ModuleCard(Module module, int x, int y, int width) {
        this.module = module;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = COLLAPSED_HEIGHT;
        this.expandAnim = new Animation(0f, Theme.ANIM_SPEED_NORMAL);
        this.toggleAnim = new Animation(module.isEnabled() ? 1f : 0f, Theme.ANIM_SPEED_FAST);
        this.hoverAnim = new Animation(0f, Theme.ANIM_SPEED_FAST);
        rebuildSettings();
    }

    private void rebuildSettings() {
        settingComponents.clear();
        int settingY = 0;
        for (Setting<?> s : module.settings.getAllSettings()) {
            if (s.isVisible()) {
                settingComponents.add(SettingComponent.create(s, 0, settingY, width - 16));
                settingY += Theme.SETTING_ROW_HEIGHT;
            }
        }
    }

    public int getFullHeight() {
        if (!expanded && expandAnim.get() < 0.01f) return COLLAPSED_HEIGHT;
        int settingsHeight = settingComponents.size() * Theme.SETTING_ROW_HEIGHT;
        int expandedH = COLLAPSED_HEIGHT + settingsHeight + 4;
        float t = expandAnim.get();
        return (int) (COLLAPSED_HEIGHT + (expandedH - COLLAPSED_HEIGHT) * t);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        toggleAnim.setTarget(module.isEnabled() ? 1f : 0f);
        toggleAnim.update();
        expandAnim.setTarget(expanded ? 1f : 0f);
        expandAnim.update();
        hoverAnim.setTarget(isHovered(mouseX, mouseY) ? 1f : 0f);
        hoverAnim.update();

        height = getFullHeight();

        // Card background
        int bgColor = Theme.lerpColor(Theme.BG_CARD, Theme.BG_CARD_HOVER, hoverAnim.get());
        GuiRenderUtil.drawRoundedRect(context, x, y, width, height, 4, bgColor);

        // Enabled accent bar on left
        float toggleT = toggleAnim.get();
        if (toggleT > 0.01f) {
            int accentColor = Theme.withAlpha(Theme.ACCENT_ENABLED, (int) (255 * toggleT));
            context.fill(x, y + 4, x + 3, y + COLLAPSED_HEIGHT - 4, accentColor);
            // Subtle glow
            int glowColor = Theme.withAlpha(Theme.GLOW_ENABLED, (int) (48 * toggleT));
            context.fill(x + 3, y + 2, x + 8, y + COLLAPSED_HEIGHT - 2, glowColor);
        }

        // Toggle indicator
        int dotSize = 8;
        int dotX = x + 10;
        int dotY = y + (COLLAPSED_HEIGHT - dotSize) / 2;
        int dotColor = Theme.lerpColor(Theme.ACCENT_DISABLED, Theme.ACCENT_ENABLED, toggleT);
        GuiRenderUtil.drawRoundedRect(context, dotX, dotY, dotSize, dotSize, 4, dotColor);

        // Pre-compute keybind position so description can clamp to it
        String bindText = bindListening ? "[...]" : "[" + KeyUtil.getKeyName(module.getKeybind()) + "]";
        int bindWidth = mc.textRenderer.getWidth(bindText);
        int bindX = x + width - bindWidth - 8;

        // Module name
        int nameColor = Theme.lerpColor(Theme.TEXT_SECONDARY, Theme.TEXT_PRIMARY, toggleT);
        context.drawText(mc.textRenderer, module.name, x + 24, y + 8, nameColor, true);

        // Description — truncate with ellipsis if it would overflow into the keybind area
        String desc = module.description;
        int descMaxWidth = bindX - (x + 24) - 8;
        if (mc.textRenderer.getWidth(desc) > descMaxWidth) {
            int target = descMaxWidth - mc.textRenderer.getWidth("...");
            while (!desc.isEmpty() && mc.textRenderer.getWidth(desc) > target) {
                desc = desc.substring(0, desc.length() - 1);
            }
            desc += "...";
        }
        context.drawText(mc.textRenderer, desc, x + 24, y + 19, Theme.TEXT_MUTED, true);

        // Keybind button
        int bindColor = bindListening ? Theme.ACCENT_TERTIARY : Theme.TEXT_MUTED;
        context.drawText(mc.textRenderer, bindText, bindX, y + (COLLAPSED_HEIGHT - 8) / 2, bindColor, true);

        // Expand indicator
        if (!settingComponents.isEmpty()) {
            String expandIcon = expanded ? "\u25BC" : "\u25B6";
            int iconX = bindX - 14;
            context.drawText(mc.textRenderer, expandIcon, iconX, y + (COLLAPSED_HEIGHT - 8) / 2, Theme.TEXT_MUTED, true);
        }

        // Settings (when expanded)
        if (expandAnim.get() > 0.01f && !settingComponents.isEmpty()) {
            int settingsStartY = y + COLLAPSED_HEIGHT;
            int offsetY = 0;

            // Settings separator line
            context.fill(x + 8, settingsStartY - 1, x + width - 8, settingsStartY, Theme.withAlpha(Theme.ACCENT_PRIMARY, 40));

            // Render settings using a stable local offset so row positions do not drift frame-to-frame.
            for (SettingComponent sc : settingComponents) {
                int compY = settingsStartY + offsetY;
                if (compY + sc.getHeight() > y + height) break;
                sc.setPosition(x + 8, compY);
                sc.setSize(width - 16, Theme.SETTING_ROW_HEIGHT);
                sc.render(context, mouseX, mouseY, delta);
                offsetY += Theme.SETTING_ROW_HEIGHT;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isHovered((int) mouseX, (int) mouseY)) return false;

        // Check keybind area
        String bindText = "[" + KeyUtil.getKeyName(module.getKeybind()) + "]";
        int bindWidth = mc.textRenderer.getWidth(bindText);
        int bindX = x + width - bindWidth - 8;
        if (mouseX >= bindX && mouseX <= bindX + bindWidth && mouseY >= y && mouseY < y + COLLAPSED_HEIGHT) {
            bindListening = !bindListening;
            return true;
        }

        // Check settings components when expanded
        if (expanded && expandAnim.get() > 0.5f) {
            for (SettingComponent sc : settingComponents) {
                if (sc.mouseClicked(mouseX, mouseY, button)) return true;
            }
        }

        // Header area click
        if (mouseY >= y && mouseY < y + COLLAPSED_HEIGHT) {
            if (button == 0) {
                // Left click: toggle module
                module.toggle();
                return true;
            } else if (button == 1) {
                // Right click: expand/collapse settings
                if (!settingComponents.isEmpty()) {
                    expanded = !expanded;
                    if (expanded) rebuildSettings();
                }
                return true;
            } else if (button == 2) {
                // Middle click: toggle drawn
                module.setDrawn(!module.isDrawn());
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (SettingComponent sc : settingComponents) {
            if (sc.mouseReleased(mouseX, mouseY, button)) return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (SettingComponent sc : settingComponents) {
            if (sc.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (bindListening) {
            bindListening = false;
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
                module.setKeybind(org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN);
            } else {
                module.setKeybind(keyCode);
            }
            return true;
        }
        for (SettingComponent sc : settingComponents) {
            if (sc.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (SettingComponent sc : settingComponents) {
            if (sc.charTyped(chr, modifiers)) return true;
        }
        return false;
    }

    public Module getModule() { return module; }
    public boolean isBindListening() { return bindListening; }

    public boolean isTextInputFocused() {
        for (SettingComponent sc : settingComponents) {
            if (sc.isTextInputFocused()) return true;
        }
        return false;
    }
}
