package dev.oblivion.client.gui.screen;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.gui.hud.HudElement;
import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class HudEditorScreen extends Screen {

    public HudEditorScreen() {
        super(Text.literal("HUD Editor"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Semi-transparent overlay
        context.fill(0, 0, this.width, this.height, 0x40000000);

        // Render all HUD elements
        OblivionClient.get().hudManager.render(context);

        // Draw outlines around each element for positioning
        for (HudElement element : OblivionClient.get().hudManager.getElements()) {
            if (element.isVisible()) {
                int outlineColor = element.isHovered(mouseX, mouseY) ? Theme.ACCENT_PRIMARY : Theme.withAlpha(Theme.ACCENT_DISABLED, 100);
                GuiRenderUtil.drawOutline(context, element.getX() - 1, element.getY() - 1,
                        element.getWidth() + 2, element.getHeight() + 2, outlineColor);
            }
        }

        // Instructions
        String instructions = "Drag elements to reposition | ESC to close";
        int textWidth = this.textRenderer.getWidth(instructions);
        GuiRenderUtil.drawRoundedRect(context, (this.width - textWidth - 16) / 2, this.height - 30, textWidth + 16, 20, 3, Theme.BG_PANEL);
        context.drawCenteredTextWithShadow(this.textRenderer, instructions, this.width / 2, this.height - 25, Theme.TEXT_SECONDARY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        OblivionClient.get().hudManager.onMouseClicked(mouseX, mouseY, button);
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        OblivionClient.get().hudManager.onMouseDragged(mouseX, mouseY);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        OblivionClient.get().hudManager.onMouseReleased();
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
