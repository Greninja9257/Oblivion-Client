package dev.oblivion.client.gui.screen;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.gui.animation.Animation;
import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class AltsScreen extends Screen {
    private final Screen parent;

    private static final int GUI_WIDTH = 380;
    private static final int GUI_MARGIN_TOP = 30;
    private static final int ROW_HEIGHT = 28;
    private static final int BUTTON_HEIGHT = 22;
    private static final int INPUT_HEIGHT = 22;

    private String inputText = "";
    private boolean inputFocused = true;
    private int cursorTick = 0;

    private int selectedIndex = -1;
    private final Animation scrollAnim = new Animation(0f, Theme.ANIM_SPEED_FAST);
    private float scrollTarget = 0;

    private String statusText = "";
    private int statusColor = Theme.TEXT_PRIMARY;
    private int statusTicks = 0;

    private int hoveredButton = -1;

    public AltsScreen(Screen parent) {
        super(Text.literal("Alts"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        scrollTarget = 0;
        scrollAnim.set(0);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x80000000);

        int guiX = (this.width - GUI_WIDTH) / 2;
        int guiY = GUI_MARGIN_TOP;
        int guiHeight = this.height - guiY * 2;

        GuiRenderUtil.drawRoundedRect(context, guiX, guiY, GUI_WIDTH, guiHeight, 6, Theme.BG_PRIMARY);
        GuiRenderUtil.drawShadow(context, guiX, guiY, GUI_WIDTH, guiHeight);

        // Header
        GuiRenderUtil.drawRoundedRect(context, guiX, guiY, GUI_WIDTH, Theme.HEADER_HEIGHT, 6, Theme.BG_HEADER);
        String title = "ALTS";
        int titleX = guiX + Theme.PADDING_LARGE;
        int titleY = guiY + (Theme.HEADER_HEIGHT - 8) / 2;
        context.drawText(this.textRenderer, title, titleX + 1, titleY, Theme.withAlpha(Theme.ACCENT_PRIMARY, 60), false);
        context.drawText(this.textRenderer, title, titleX, titleY, Theme.ACCENT_PRIMARY, true);

        List<String> accounts = OblivionClient.get().accountManager.getOfflineAccounts();
        int titleW = this.textRenderer.getWidth(title);
        context.drawText(this.textRenderer, accounts.size() + " accounts", titleX + titleW + 8, titleY, Theme.TEXT_MUTED, true);

        String loggedIn = "Logged in: " + (this.client != null ? this.client.getSession().getUsername() : "?");
        int loggedW = this.textRenderer.getWidth(loggedIn);
        context.drawText(this.textRenderer, loggedIn, guiX + GUI_WIDTH - loggedW - Theme.PADDING, titleY, Theme.TEXT_SECONDARY, true);

        // Status toast
        int statusOffset = 0;
        if (statusTicks > 0) {
            int alpha = Math.min(255, statusTicks * 5);
            context.drawText(this.textRenderer, statusText, guiX + Theme.PADDING_LARGE, guiY + Theme.HEADER_HEIGHT + 4, Theme.withAlpha(statusColor, alpha), true);
            statusOffset = 16;
        }

        int contentY = guiY + Theme.HEADER_HEIGHT + 6 + statusOffset;
        int contentX = guiX + Theme.PADDING;
        int contentW = GUI_WIDTH - Theme.PADDING * 2;

        // Input field
        int inputW = contentW - 160;
        drawInputField(context, contentX, contentY, inputW, INPUT_HEIGHT, inputText, inputFocused, "Enter alt name...", mouseX, mouseY);

        // Buttons
        hoveredButton = -1;
        int btnX = contentX + inputW + 4;
        drawButton(context, btnX, contentY, 48, BUTTON_HEIGHT, "Add", 0, mouseX, mouseY, false);
        drawButton(context, btnX + 52, contentY, 98, BUTTON_HEIGHT, "Add Current", 1, mouseX, mouseY, false);

        int btn2Y = contentY + INPUT_HEIGHT + 6;
        drawButton(context, contentX, btn2Y, 100, BUTTON_HEIGHT, "Use Selected", 2, mouseX, mouseY, selectedIndex < 0 || selectedIndex >= accounts.size());
        drawButton(context, contentX + 104, btn2Y, 116, BUTTON_HEIGHT, "Remove Selected", 3, mouseX, mouseY, selectedIndex < 0 || selectedIndex >= accounts.size());
        drawButton(context, contentX + contentW - 56, btn2Y, 56, BUTTON_HEIGHT, "Done", 4, mouseX, mouseY, false);

        // List
        int listTop = btn2Y + BUTTON_HEIGHT + 10;
        int listBottom = guiY + guiHeight - Theme.PADDING - 2;
        int listHeight = listBottom - listTop;

        context.drawText(this.textRenderer, "Saved Alts", contentX + 2, listTop - 12, Theme.TEXT_SECONDARY, true);
        GuiRenderUtil.drawRoundedRect(context, contentX, listTop, contentW, listHeight, 4, Theme.BG_SECONDARY);

        scrollAnim.setTarget(scrollTarget);
        scrollAnim.update();
        float scroll = scrollAnim.get();

        int contentHeight = accounts.size() * ROW_HEIGHT;
        float maxScroll = Math.max(0, contentHeight - listHeight);
        if (scrollTarget > maxScroll) {
            scrollTarget = maxScroll;
            scrollAnim.setTarget(scrollTarget);
        }

        context.enableScissor(contentX, listTop, contentX + contentW, listBottom);

        String activeName = OblivionClient.get().accountManager.getActiveOfflineName();
        int rowY = listTop - (int) scroll;
        for (int i = 0; i < accounts.size(); i++) {
            if (rowY + ROW_HEIGHT > listTop && rowY < listBottom) {
                String name = accounts.get(i);
                boolean selected = i == selectedIndex;
                boolean active = name.equals(activeName);
                boolean rowHovered = mouseX >= contentX && mouseX <= contentX + contentW - Theme.SCROLLBAR_WIDTH - 6
                    && mouseY >= Math.max(rowY, listTop) && mouseY <= Math.min(rowY + ROW_HEIGHT, listBottom);

                int rowBg = selected ? Theme.BG_CARD_HOVER : (rowHovered ? Theme.BG_CARD : Theme.withAlpha(Theme.BG_CARD, 80));
                int rw = contentW - 4 - Theme.SCROLLBAR_WIDTH - 4;
                GuiRenderUtil.drawRoundedRect(context, contentX + 2, rowY + 1, rw, ROW_HEIGHT - 2, 3, rowBg);

                if (selected) {
                    GuiRenderUtil.drawOutline(context, contentX + 2, rowY + 1, rw, ROW_HEIGHT - 2, Theme.withAlpha(Theme.ACCENT_PRIMARY, 120));
                }

                if (active) {
                    int dotY = rowY + (ROW_HEIGHT - 6) / 2;
                    context.fill(contentX + 8, dotY, contentX + 14, dotY + 6, Theme.ACCENT_ENABLED);
                }

                int textX = contentX + (active ? 20 : 10);
                context.drawText(this.textRenderer, name, textX, rowY + (ROW_HEIGHT - 8) / 2, selected ? Theme.TEXT_PRIMARY : Theme.TEXT_SECONDARY, true);

                if (active) {
                    int tagX = contentX + rw - 38;
                    context.drawText(this.textRenderer, "ACTIVE", tagX, rowY + (ROW_HEIGHT - 8) / 2, Theme.TEXT_ENABLED, true);
                }
            }
            rowY += ROW_HEIGHT;
        }

        context.disableScissor();

        // Scrollbar
        if (contentHeight > listHeight) {
            int scrollbarX = contentX + contentW - Theme.SCROLLBAR_WIDTH - 2;
            float scrollRatio = scroll / Math.max(1, contentHeight - listHeight);
            int scrollbarH = Math.max(20, (int) ((float) listHeight / contentHeight * listHeight));
            int scrollbarY = listTop + (int) ((listHeight - scrollbarH) * scrollRatio);
            context.fill(scrollbarX, listTop, scrollbarX + Theme.SCROLLBAR_WIDTH, listBottom, Theme.withAlpha(Theme.BG_CARD, 100));
            GuiRenderUtil.drawRoundedRect(context, scrollbarX, scrollbarY, Theme.SCROLLBAR_WIDTH, scrollbarH, 1, Theme.ACCENT_PRIMARY);
        }

        // Bottom accent
        GuiRenderUtil.drawGradientRect(context, guiX + 20, guiY + guiHeight - 2, GUI_WIDTH - 40, 2,
            Theme.withAlpha(Theme.ACCENT_PRIMARY, 0), Theme.ACCENT_PRIMARY);
    }

    private void drawInputField(DrawContext ctx, int x, int y, int w, int h, String text, boolean focused, String placeholder, int mx, int my) {
        GuiRenderUtil.drawRoundedRect(ctx, x, y, w, h, 3, focused ? Theme.BG_CARD_HOVER : Theme.BG_CARD);
        if (focused) GuiRenderUtil.drawOutline(ctx, x, y, w, h, Theme.withAlpha(Theme.ACCENT_PRIMARY, 150));

        String display = text.isEmpty() && !focused ? placeholder : text;
        int color = text.isEmpty() && !focused ? Theme.TEXT_MUTED : Theme.TEXT_PRIMARY;
        int textY = y + (h - 8) / 2;

        String clipped = display;
        while (this.textRenderer.getWidth(clipped) > w - 12 && clipped.length() > 1) clipped = clipped.substring(1);
        ctx.drawText(this.textRenderer, clipped, x + 6, textY, color, true);

        if (focused && (cursorTick / 10) % 2 == 0) {
            int cx = x + 6 + this.textRenderer.getWidth(text);
            if (cx < x + w - 4) ctx.fill(cx, textY - 1, cx + 1, textY + 9, Theme.TEXT_PRIMARY);
        }
    }

    private void drawButton(DrawContext ctx, int x, int y, int w, int h, String label, int id, int mx, int my, boolean disabled) {
        boolean hovered = mx >= x && mx <= x + w && my >= y && my <= y + h && !disabled;
        if (hovered) hoveredButton = id;
        GuiRenderUtil.drawRoundedRect(ctx, x, y, w, h, 3, disabled ? Theme.withAlpha(Theme.BG_CARD, 100) : (hovered ? Theme.BG_CARD_HOVER : Theme.BG_CARD));
        if (hovered) GuiRenderUtil.drawOutline(ctx, x, y, w, h, Theme.withAlpha(Theme.ACCENT_PRIMARY, 80));
        int tc = disabled ? Theme.TEXT_MUTED : (hovered ? Theme.ACCENT_PRIMARY : Theme.TEXT_PRIMARY);
        int tw = this.textRenderer.getWidth(label);
        ctx.drawText(this.textRenderer, label, x + (w - tw) / 2, y + (h - 8) / 2, tc, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int guiX = (this.width - GUI_WIDTH) / 2;
        int guiY = GUI_MARGIN_TOP;
        int contentX = guiX + Theme.PADDING;
        int contentW = GUI_WIDTH - Theme.PADDING * 2;
        int statusOffset = statusTicks > 0 ? 16 : 0;
        int contentY = guiY + Theme.HEADER_HEIGHT + 6 + statusOffset;
        int inputW = contentW - 160;

        if (mouseX >= contentX && mouseX <= contentX + inputW && mouseY >= contentY && mouseY <= contentY + INPUT_HEIGHT) {
            inputFocused = true;
            return true;
        } else {
            inputFocused = false;
        }

        if (hoveredButton == 0) { addAlt(); return true; }
        if (hoveredButton == 1) { addCurrent(); return true; }
        if (hoveredButton == 2) { useSelected(); return true; }
        if (hoveredButton == 3) { removeSelected(); return true; }
        if (hoveredButton == 4) { close(); return true; }

        // List click
        int btn2Y = contentY + INPUT_HEIGHT + 6;
        int listTop = btn2Y + BUTTON_HEIGHT + 10;
        int guiHeight = this.height - guiY * 2;
        int listBottom = guiY + guiHeight - Theme.PADDING - 2;
        float scroll = scrollAnim.get();

        if (mouseX >= contentX && mouseX <= contentX + contentW && mouseY >= listTop && mouseY <= listBottom) {
            int clickedRow = (int) ((mouseY - listTop + scroll) / ROW_HEIGHT);
            List<String> accounts = OblivionClient.get().accountManager.getOfflineAccounts();
            if (clickedRow >= 0 && clickedRow < accounts.size()) {
                selectedIndex = clickedRow;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        List<String> accounts = OblivionClient.get().accountManager.getOfflineAccounts();
        int guiY = GUI_MARGIN_TOP;
        int guiHeight = this.height - guiY * 2;
        int statusOffset = statusTicks > 0 ? 16 : 0;
        int contentY = guiY + Theme.HEADER_HEIGHT + 6 + statusOffset;
        int btn2Y = contentY + INPUT_HEIGHT + 6;
        int listTop = btn2Y + BUTTON_HEIGHT + 10;
        int listBottom = guiY + guiHeight - Theme.PADDING - 2;
        int listHeight = listBottom - listTop;
        int contentHeight = accounts.size() * ROW_HEIGHT;
        float maxScroll = Math.max(0, contentHeight - listHeight);
        scrollTarget -= (float) verticalAmount * 24;
        scrollTarget = Math.max(0, Math.min(maxScroll, scrollTarget));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (inputFocused) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) { addAlt(); return true; }
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !inputText.isEmpty()) { inputText = inputText.substring(0, inputText.length() - 1); return true; }
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) { inputFocused = false; return true; }
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) { close(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (inputFocused && inputText.length() < 32 && chr >= 32) { inputText += chr; return true; }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void tick() { super.tick(); cursorTick++; if (statusTicks > 0) statusTicks--; }

    private void addAlt() {
        String name = inputText.trim();
        if (name.isEmpty()) { setStatus("Enter an alt name first.", Theme.NOTIFY_DISABLED); return; }
        boolean exists = OblivionClient.get().accountManager.getOfflineAccounts().stream().anyMatch(a -> a.equalsIgnoreCase(name));
        if (exists) { setStatus("Already exists: " + name, Theme.NOTIFY_WARNING); return; }
        OblivionClient.get().accountManager.addOffline(name);
        inputText = "";
        selectByName(name);
        setStatus("Added: " + name, Theme.NOTIFY_ENABLED);
    }

    private void addCurrent() {
        if (this.client == null) return;
        String name = this.client.getSession().getUsername();
        boolean added = OblivionClient.get().accountManager.addOffline(name);
        selectByName(name);
        setStatus(added ? "Added current: " + name : "Already exists: " + name, added ? Theme.NOTIFY_ENABLED : Theme.NOTIFY_WARNING);
    }

    private void useSelected() {
        List<String> a = OblivionClient.get().accountManager.getOfflineAccounts();
        if (selectedIndex >= 0 && selectedIndex < a.size()) {
            OblivionClient.get().accountManager.useOffline(a.get(selectedIndex));
            setStatus("Switched to: " + a.get(selectedIndex), Theme.NOTIFY_ENABLED);
        }
    }

    private void removeSelected() {
        List<String> a = OblivionClient.get().accountManager.getOfflineAccounts();
        if (selectedIndex >= 0 && selectedIndex < a.size()) {
            String r = a.get(selectedIndex);
            OblivionClient.get().accountManager.removeOffline(r);
            selectedIndex = -1;
            setStatus("Removed: " + r, Theme.NOTIFY_DISABLED);
        }
    }

    private void setStatus(String text, int color) { statusText = text; statusColor = color; statusTicks = 80; }

    private void selectByName(String name) {
        List<String> a = OblivionClient.get().accountManager.getOfflineAccounts();
        for (int i = 0; i < a.size(); i++) { if (a.get(i).equalsIgnoreCase(name)) { selectedIndex = i; return; } }
    }

    @Override
    public void close() { if (this.client != null) this.client.setScreen(this.parent); }

    @Override
    public boolean shouldPause() { return false; }
}
