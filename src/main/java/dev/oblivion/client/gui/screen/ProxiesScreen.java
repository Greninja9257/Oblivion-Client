package dev.oblivion.client.gui.screen;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.gui.animation.Animation;
import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import dev.oblivion.client.proxy.ProxyManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ProxiesScreen extends Screen {
    private final Screen parent;

    private static final int GUI_WIDTH = 420;
    private static final int GUI_MARGIN_TOP = 20;
    private static final int ROW_HEIGHT = 32;
    private static final int BUTTON_HEIGHT = 22;
    private static final int INPUT_HEIGHT = 22;

    // Input fields
    private final String[] fieldTexts = {"", "", "", "", ""};
    private final String[] fieldPlaceholders = {"Name", "Host", "Port", "User", "Pass"};
    private final int[] fieldMaxLens = {32, 128, 5, 64, 64};
    private int focusedField = -1;
    private int cursorTick = 0;

    private ProxyManager.ProxyType currentType = ProxyManager.ProxyType.SOCKS5;
    private int selectedIndex = -1;
    private final Animation scrollAnim = new Animation(0f, Theme.ANIM_SPEED_FAST);
    private float scrollTarget = 0;

    private String statusText = "";
    private int statusColor = Theme.TEXT_PRIMARY;
    private int statusTicks = 0;

    private boolean busy = false;
    private int hoveredButton = -1;

    public ProxiesScreen(Screen parent) {
        super(Text.literal("Proxies"));
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
        String title = "PROXIES";
        int titleX = guiX + Theme.PADDING_LARGE;
        int titleY = guiY + (Theme.HEADER_HEIGHT - 8) / 2;
        context.drawText(this.textRenderer, title, titleX + 1, titleY, Theme.withAlpha(Theme.ACCENT_PRIMARY, 60), false);
        context.drawText(this.textRenderer, title, titleX, titleY, Theme.ACCENT_PRIMARY, true);

        List<ProxyManager.ProxyEntry> proxies = OblivionClient.get().proxyManager.getProxies();
        int titleW = this.textRenderer.getWidth(title);
        context.drawText(this.textRenderer, proxies.size() + " proxies", titleX + titleW + 8, titleY, Theme.TEXT_MUTED, true);

        ProxyManager.ProxyEntry activeProxy = OblivionClient.get().proxyManager.getActive();
        String activeStr = activeProxy != null ? "Active: " + activeProxy.name : "No proxy active";
        int activeW = this.textRenderer.getWidth(activeStr);
        context.drawText(this.textRenderer, activeStr, guiX + GUI_WIDTH - activeW - Theme.PADDING, titleY, Theme.TEXT_SECONDARY, true);

        // Status toast
        int statusOffset = 0;
        if (statusTicks > 0) {
            int alpha = Math.min(255, statusTicks * 5);
            context.drawText(this.textRenderer, statusText, guiX + Theme.PADDING_LARGE, guiY + Theme.HEADER_HEIGHT + 4, Theme.withAlpha(statusColor, alpha), true);
            statusOffset = 14;
        }

        int contentY = guiY + Theme.HEADER_HEIGHT + 4 + statusOffset;
        int contentX = guiX + Theme.PADDING;
        int contentW = GUI_WIDTH - Theme.PADDING * 2;

        // Row 1: Name, Host, Port
        int nameW = 100;
        int hostW = 160;
        int portW = contentW - nameW - hostW - 8;
        drawInputField(context, contentX, contentY, nameW, INPUT_HEIGHT, fieldTexts[0], focusedField == 0, fieldPlaceholders[0], mouseX, mouseY);
        drawInputField(context, contentX + nameW + 4, contentY, hostW, INPUT_HEIGHT, fieldTexts[1], focusedField == 1, fieldPlaceholders[1], mouseX, mouseY);
        drawInputField(context, contentX + nameW + hostW + 8, contentY, portW, INPUT_HEIGHT, fieldTexts[2], focusedField == 2, fieldPlaceholders[2], mouseX, mouseY);

        // Row 2: User, Pass
        int row2Y = contentY + INPUT_HEIGHT + 4;
        int halfW = (contentW - 4) / 2;
        drawInputField(context, contentX, row2Y, halfW, INPUT_HEIGHT, fieldTexts[3], focusedField == 3, fieldPlaceholders[3], mouseX, mouseY);
        drawInputField(context, contentX + halfW + 4, row2Y, contentW - halfW - 4, INPUT_HEIGHT, fieldTexts[4], focusedField == 4, fieldPlaceholders[4], mouseX, mouseY);

        // Row 3: Type toggle, Add, Disable
        hoveredButton = -1;
        int row3Y = row2Y + INPUT_HEIGHT + 6;
        drawButton(context, contentX, row3Y, 90, BUTTON_HEIGHT, "Type: " + currentType.name(), 0, mouseX, mouseY, busy);
        drawButton(context, contentX + 94, row3Y, 70, BUTTON_HEIGHT, "Add", 1, mouseX, mouseY, busy);
        drawButton(context, contentX + 168, row3Y, 70, BUTTON_HEIGHT, "Disable", 2, mouseX, mouseY, busy);

        // Row 4: Fetch Fast, Test & Prune
        int row4Y = row3Y + BUTTON_HEIGHT + 4;
        drawButton(context, contentX, row4Y, 100, BUTTON_HEIGHT, busy ? "Working..." : "Fetch Fast", 3, mouseX, mouseY, busy);
        drawButton(context, contentX + 104, row4Y, 110, BUTTON_HEIGHT, busy ? "Working..." : "Test & Prune", 4, mouseX, mouseY, busy);

        // Row 5: Use, Remove, Done
        int row5Y = row4Y + BUTTON_HEIGHT + 4;
        boolean selValid = selectedIndex >= 0 && selectedIndex < proxies.size();
        drawButton(context, contentX, row5Y, 100, BUTTON_HEIGHT, "Use Selected", 5, mouseX, mouseY, !selValid || busy);
        drawButton(context, contentX + 104, row5Y, 116, BUTTON_HEIGHT, "Remove Selected", 6, mouseX, mouseY, !selValid || busy);
        drawButton(context, contentX + contentW - 56, row5Y, 56, BUTTON_HEIGHT, "Done", 7, mouseX, mouseY, false);

        // List
        int listTop = row5Y + BUTTON_HEIGHT + 10;
        int listBottom = guiY + guiHeight - Theme.PADDING - 2;
        int listHeight = listBottom - listTop;

        context.drawText(this.textRenderer, "Saved Proxies", contentX + 2, listTop - 12, Theme.TEXT_SECONDARY, true);
        GuiRenderUtil.drawRoundedRect(context, contentX, listTop, contentW, listHeight, 4, Theme.BG_SECONDARY);

        scrollAnim.setTarget(scrollTarget);
        scrollAnim.update();
        float scroll = scrollAnim.get();

        int contentHeight = proxies.size() * ROW_HEIGHT;
        float maxScroll = Math.max(0, contentHeight - listHeight);
        if (scrollTarget > maxScroll) {
            scrollTarget = maxScroll;
            scrollAnim.setTarget(scrollTarget);
        }

        context.enableScissor(contentX, listTop, contentX + contentW, listBottom);

        int activeIndex = OblivionClient.get().proxyManager.getActiveIndex();
        int rowY = listTop - (int) scroll;
        for (int i = 0; i < proxies.size(); i++) {
            if (rowY + ROW_HEIGHT > listTop && rowY < listBottom) {
                ProxyManager.ProxyEntry p = proxies.get(i);
                boolean selected = i == selectedIndex;
                boolean active = i == activeIndex;
                boolean rowHovered = mouseX >= contentX && mouseX <= contentX + contentW - Theme.SCROLLBAR_WIDTH - 6
                    && mouseY >= Math.max(rowY, listTop) && mouseY <= Math.min(rowY + ROW_HEIGHT, listBottom);

                int rowBg = selected ? Theme.BG_CARD_HOVER : (rowHovered ? Theme.BG_CARD : Theme.withAlpha(Theme.BG_CARD, 80));
                int rw = contentW - 4 - Theme.SCROLLBAR_WIDTH - 4;
                GuiRenderUtil.drawRoundedRect(context, contentX + 2, rowY + 1, rw, ROW_HEIGHT - 2, 3, rowBg);

                if (selected) {
                    GuiRenderUtil.drawOutline(context, contentX + 2, rowY + 1, rw, ROW_HEIGHT - 2, Theme.withAlpha(Theme.ACCENT_PRIMARY, 120));
                }

                // Active dot
                if (active) {
                    int dotY = rowY + (ROW_HEIGHT - 6) / 2;
                    context.fill(contentX + 8, dotY, contentX + 14, dotY + 6, Theme.ACCENT_ENABLED);
                }

                int textX = contentX + (active ? 20 : 10);

                // Name
                context.drawText(this.textRenderer, p.name, textX, rowY + 4, selected ? Theme.TEXT_PRIMARY : Theme.TEXT_SECONDARY, true);

                // Type badge
                String typeBadge = p.type.name();
                int badgeColor = p.type == ProxyManager.ProxyType.SOCKS5 ? Theme.ACCENT_PRIMARY : Theme.NOTIFY_WARNING;
                context.drawText(this.textRenderer, typeBadge, textX + this.textRenderer.getWidth(p.name) + 6, rowY + 4, badgeColor, true);

                // Host:port on second line
                String hostPort = p.host + ":" + p.port;
                context.drawText(this.textRenderer, hostPort, textX, rowY + 16, Theme.TEXT_MUTED, true);

                // Active tag
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
        int statusOffset = statusTicks > 0 ? 14 : 0;
        int contentY = guiY + Theme.HEADER_HEIGHT + 4 + statusOffset;

        // Check input field clicks
        int nameW = 100;
        int hostW = 160;
        int portW = contentW - nameW - hostW - 8;
        int halfW = (contentW - 4) / 2;

        int[][] fieldBounds = {
            {contentX, contentY, nameW, INPUT_HEIGHT},
            {contentX + nameW + 4, contentY, hostW, INPUT_HEIGHT},
            {contentX + nameW + hostW + 8, contentY, portW, INPUT_HEIGHT},
            {contentX, contentY + INPUT_HEIGHT + 4, halfW, INPUT_HEIGHT},
            {contentX + halfW + 4, contentY + INPUT_HEIGHT + 4, contentW - halfW - 4, INPUT_HEIGHT},
        };

        focusedField = -1;
        for (int i = 0; i < fieldBounds.length; i++) {
            int[] b = fieldBounds[i];
            if (mouseX >= b[0] && mouseX <= b[0] + b[2] && mouseY >= b[1] && mouseY <= b[1] + b[3]) {
                focusedField = i;
                return true;
            }
        }

        // Button clicks
        if (hoveredButton == 0) { toggleType(); return true; }
        if (hoveredButton == 1) { addProxy(); return true; }
        if (hoveredButton == 2) { disableProxy(); return true; }
        if (hoveredButton == 3) { fetchFastProxies(); return true; }
        if (hoveredButton == 4) { testAndPrune(); return true; }
        if (hoveredButton == 5) { useSelected(); return true; }
        if (hoveredButton == 6) { removeSelected(); return true; }
        if (hoveredButton == 7) { close(); return true; }

        // List click
        List<ProxyManager.ProxyEntry> proxies = OblivionClient.get().proxyManager.getProxies();
        int row2Y = contentY + INPUT_HEIGHT + 4;
        int row3Y = row2Y + INPUT_HEIGHT + 6;
        int row4Y = row3Y + BUTTON_HEIGHT + 4;
        int row5Y = row4Y + BUTTON_HEIGHT + 4;
        int listTop = row5Y + BUTTON_HEIGHT + 10;
        int guiHeight = this.height - guiY * 2;
        int listBottom = guiY + guiHeight - Theme.PADDING - 2;
        float scroll = scrollAnim.get();

        if (mouseX >= contentX && mouseX <= contentX + contentW && mouseY >= listTop && mouseY <= listBottom) {
            int clickedRow = (int) ((mouseY - listTop + scroll) / ROW_HEIGHT);
            if (clickedRow >= 0 && clickedRow < proxies.size()) {
                selectedIndex = clickedRow;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        List<ProxyManager.ProxyEntry> proxies = OblivionClient.get().proxyManager.getProxies();
        int guiY = GUI_MARGIN_TOP;
        int guiHeight = this.height - guiY * 2;
        int statusOffset = statusTicks > 0 ? 14 : 0;
        int contentY = guiY + Theme.HEADER_HEIGHT + 4 + statusOffset;
        int row2Y = contentY + INPUT_HEIGHT + 4;
        int row3Y = row2Y + INPUT_HEIGHT + 6;
        int row4Y = row3Y + BUTTON_HEIGHT + 4;
        int row5Y = row4Y + BUTTON_HEIGHT + 4;
        int listTop = row5Y + BUTTON_HEIGHT + 10;
        int listBottom = guiY + guiHeight - Theme.PADDING - 2;
        int listHeight = listBottom - listTop;
        int contentHeight = proxies.size() * ROW_HEIGHT;
        float maxScroll = Math.max(0, contentHeight - listHeight);
        scrollTarget -= (float) verticalAmount * 24;
        scrollTarget = Math.max(0, Math.min(maxScroll, scrollTarget));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (focusedField >= 0) {
            if (keyCode == GLFW.GLFW_KEY_TAB) {
                focusedField = (focusedField + 1) % 5;
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) { addProxy(); return true; }
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !fieldTexts[focusedField].isEmpty()) {
                fieldTexts[focusedField] = fieldTexts[focusedField].substring(0, fieldTexts[focusedField].length() - 1);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) { focusedField = -1; return true; }
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) { close(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (focusedField >= 0 && fieldTexts[focusedField].length() < fieldMaxLens[focusedField] && chr >= 32) {
            fieldTexts[focusedField] += chr;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void tick() {
        super.tick();
        cursorTick++;
        if (statusTicks > 0) statusTicks--;
    }

    private void toggleType() {
        currentType = currentType == ProxyManager.ProxyType.SOCKS5 ? ProxyManager.ProxyType.HTTP : ProxyManager.ProxyType.SOCKS5;
    }

    private void addProxy() {
        String name = fieldTexts[0].trim();
        String host = fieldTexts[1].trim();
        String portRaw = fieldTexts[2].trim();
        String user = fieldTexts[3].trim();
        String pass = fieldTexts[4];

        if (name.isEmpty() || host.isEmpty()) {
            setStatus("Name and host are required.", Theme.NOTIFY_DISABLED);
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portRaw);
        } catch (Exception e) {
            setStatus("Invalid port number.", Theme.NOTIFY_DISABLED);
            return;
        }

        boolean ok = OblivionClient.get().proxyManager.add(new ProxyManager.ProxyEntry(name, currentType, host, port, user, pass));
        if (!ok) {
            setStatus("Failed to add (duplicate or invalid).", Theme.NOTIFY_WARNING);
            return;
        }

        for (int i = 0; i < fieldTexts.length; i++) fieldTexts[i] = "";
        selectedIndex = OblivionClient.get().proxyManager.getProxies().size() - 1;
        setStatus("Added proxy: " + name, Theme.NOTIFY_ENABLED);
    }

    private void disableProxy() {
        OblivionClient.get().proxyManager.disable();
        setStatus("Proxy disabled.", Theme.ACCENT_PRIMARY);
    }

    private void useSelected() {
        List<ProxyManager.ProxyEntry> proxies = OblivionClient.get().proxyManager.getProxies();
        if (selectedIndex >= 0 && selectedIndex < proxies.size()) {
            OblivionClient.get().proxyManager.use(selectedIndex);
            setStatus("Using: " + proxies.get(selectedIndex).name, Theme.NOTIFY_ENABLED);
        }
    }

    private void removeSelected() {
        List<ProxyManager.ProxyEntry> proxies = OblivionClient.get().proxyManager.getProxies();
        if (selectedIndex >= 0 && selectedIndex < proxies.size()) {
            String name = proxies.get(selectedIndex).name;
            OblivionClient.get().proxyManager.remove(selectedIndex);
            selectedIndex = -1;
            setStatus("Removed: " + name, Theme.NOTIFY_DISABLED);
        }
    }

    private void fetchFastProxies() {
        if (busy) return;
        busy = true;
        setStatus("Fetching fast proxies...", Theme.ACCENT_PRIMARY);

        Thread t = new Thread(() -> {
            ProxyManager.FetchReport r = OblivionClient.get().proxyManager.fetchFastOnlineProxies(60, 1800);
            if (this.client != null) {
                this.client.execute(() -> {
                    busy = false;
                    setStatus("Fetched " + r.fetchedUnique() + " unique, added " + r.added() + ", skipped " + r.skipped(), Theme.NOTIFY_ENABLED);
                });
            } else {
                busy = false;
            }
        }, "Oblivion-ProxyFetch");
        t.setDaemon(true);
        t.start();
    }

    private void testAndPrune() {
        if (busy) return;
        busy = true;
        setStatus("Testing & pruning proxies...", Theme.ACCENT_PRIMARY);

        Thread t = new Thread(() -> {
            ProxyManager.PruneReport r = OblivionClient.get().proxyManager.pruneBadAndSlow(1600, 1.35);
            if (this.client != null) {
                this.client.execute(() -> {
                    busy = false;
                    if (selectedIndex >= OblivionClient.get().proxyManager.getProxies().size()) selectedIndex = -1;
                    setStatus(String.format("Alive %d, dead %d, slow %d, avg %.0fms",
                        r.alive(), r.removedDead(), r.removedSlow(), r.averageMs()), Theme.NOTIFY_ENABLED);
                });
            } else {
                busy = false;
            }
        }, "Oblivion-ProxyPrune");
        t.setDaemon(true);
        t.start();
    }

    private void setStatus(String text, int color) {
        statusText = text;
        statusColor = color;
        statusTicks = 100;
    }

    @Override
    public void close() {
        if (this.client != null) this.client.setScreen(this.parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
