package dev.oblivion.client.gui.screen;

import com.google.gson.JsonObject;
import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.gui.animation.Animation;
import dev.oblivion.client.gui.component.MarketplacePanel;
import dev.oblivion.client.gui.component.ModuleCard;
import dev.oblivion.client.gui.component.SearchBar;
import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private static Category persistedCategory = Category.COMBAT;
    private static float persistedScroll = 0f;

    private Category selectedCategory = persistedCategory;
    private final List<ModuleCard> moduleCards = new ArrayList<>();
    private SearchBar searchBar;
    private final Animation scrollAnim = new Animation(0f, Theme.ANIM_SPEED_FAST);
    private final Animation[] tabAnimations = new Animation[Category.values().length];
    private float scrollTarget = 0;
    private int contentHeight = 0;

    // Horizontal tab scroll
    private float tabScrollOffset = 0f;
    private float tabScrollTarget = 0f;
    private final Animation tabScrollAnim = new Animation(0f, Theme.ANIM_SPEED_FAST);
    private int totalTabsWidth = 0;

    // Marketplace view state
    private boolean marketplaceOpen = false;
    private MarketplacePanel marketplacePanel;
    private SearchBar marketplaceSearchBar;

    private static final int GUI_WIDTH = 380;
    private static final int GUI_MARGIN_TOP = 30;
    private static final int CARD_SPACING = 4;

    // Marketplace button dimensions (in header, right of version)
    private int mpBtnX, mpBtnY, mpBtnW, mpBtnH;

    public ClickGuiScreen() {
        super(Text.literal("Oblivion ClickGUI"));
        for (int i = 0; i < tabAnimations.length; i++) {
            tabAnimations[i] = new Animation(0f, Theme.ANIM_SPEED_FAST);
        }
    }

    @Override
    protected void init() {
        int guiX = (this.width - GUI_WIDTH) / 2;
        searchBar = new SearchBar(guiX + Theme.PADDING, GUI_MARGIN_TOP + Theme.HEADER_HEIGHT + Theme.PADDING_SMALL,
                GUI_WIDTH - Theme.PADDING * 2);
        scrollTarget = persistedScroll;
        scrollAnim.setTarget(scrollTarget);
        computeTotalTabsWidth();
        rebuildCards();
    }

    private void computeTotalTabsWidth() {
        totalTabsWidth = 0;
        for (Category cat : Category.values()) {
            totalTabsWidth += this.textRenderer.getWidth(cat.displayName) + 16 + 4;
        }
        totalTabsWidth -= 4;
    }

    private void rebuildCards() {
        moduleCards.clear();
        int guiX = (this.width - GUI_WIDTH) / 2;
        int cardWidth = GUI_WIDTH - Theme.PADDING * 2 - Theme.SCROLLBAR_WIDTH - 2;

        List<Module> modules = getFilteredModules();
        for (Module m : modules) {
            moduleCards.add(new ModuleCard(m, guiX + Theme.PADDING, 0, cardWidth));
        }
    }

    private List<Module> getFilteredModules() {
        String search = searchBar != null ? searchBar.getText().toLowerCase() : "";
        List<Module> result = new ArrayList<>();

        if (search.isEmpty()) {
            result.addAll(OblivionClient.get().moduleManager.getByCategory(selectedCategory));
        } else {
            for (Module m : OblivionClient.get().moduleManager.getAll()) {
                if (m.name.toLowerCase().contains(search) || m.description.toLowerCase().contains(search)) {
                    result.add(m);
                }
            }
        }

        result.sort(Comparator.comparing(m -> m.name, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Dim background
        context.fill(0, 0, this.width, this.height, 0x80000000);

        int guiX = (this.width - GUI_WIDTH) / 2;
        int guiY = GUI_MARGIN_TOP;
        int guiHeight = this.height - guiY * 2;

        // Main panel background
        GuiRenderUtil.drawRoundedRect(context, guiX, guiY, GUI_WIDTH, guiHeight, 6, Theme.BG_PRIMARY);
        GuiRenderUtil.drawShadow(context, guiX, guiY, GUI_WIDTH, guiHeight);

        // Header
        GuiRenderUtil.drawRoundedRect(context, guiX, guiY, GUI_WIDTH, Theme.HEADER_HEIGHT, 6, Theme.BG_HEADER);

        int titleY = guiY + (Theme.HEADER_HEIGHT - 8) / 2;

        if (marketplaceOpen) {
            renderMarketplaceContent(context, mouseX, mouseY, delta, guiX, guiY, guiHeight, titleY);
        } else {
            renderClickGuiContent(context, mouseX, mouseY, delta, guiX, guiY, guiHeight, titleY);
        }

        // Bottom accent line (same for both modes)
        int accentColor = Theme.ACCENT_PRIMARY;
        int bottomY = guiY + guiHeight - 2;
        GuiRenderUtil.drawGradientRect(context, guiX + 20, bottomY, GUI_WIDTH - 40, 2,
                Theme.withAlpha(accentColor, 0), accentColor);
    }

    private void renderClickGuiContent(DrawContext context, int mouseX, int mouseY, float delta,
                                       int guiX, int guiY, int guiHeight, int titleY) {
        // Title with glow
        String title = "OBLIVION";
        int titleWidth = this.textRenderer.getWidth(title);
        int titleX = guiX + Theme.PADDING_LARGE;
        context.drawText(this.textRenderer, title, titleX + 1, titleY, Theme.withAlpha(Theme.ACCENT_PRIMARY, 60), false);
        context.drawText(this.textRenderer, title, titleX, titleY, Theme.ACCENT_PRIMARY, true);

        // Version
        String version = "v" + OblivionClient.VERSION;
        context.drawText(this.textRenderer, version, titleX + titleWidth + 4, titleY, Theme.TEXT_MUTED, true);

        // Marketplace button in header (right side, before ESC hint)
        String mpLabel = "\u2302 Market";
        mpBtnW = this.textRenderer.getWidth(mpLabel) + 12;
        mpBtnH = 16;
        String hint = "ESC";
        int hintWidth = this.textRenderer.getWidth(hint);
        mpBtnX = guiX + GUI_WIDTH - hintWidth - Theme.PADDING - mpBtnW - 8;
        mpBtnY = guiY + (Theme.HEADER_HEIGHT - mpBtnH) / 2;

        boolean mpBtnHovered = mouseX >= mpBtnX && mouseX <= mpBtnX + mpBtnW
                && mouseY >= mpBtnY && mouseY <= mpBtnY + mpBtnH;
        int mpBtnBg = mpBtnHovered ? Theme.withAlpha(Theme.ACCENT_SECONDARY, 50) : Theme.withAlpha(Theme.ACCENT_SECONDARY, 25);
        GuiRenderUtil.drawRoundedRect(context, mpBtnX, mpBtnY, mpBtnW, mpBtnH, 3, mpBtnBg);
        GuiRenderUtil.drawOutline(context, mpBtnX, mpBtnY, mpBtnW, mpBtnH, Theme.withAlpha(Theme.ACCENT_SECONDARY, mpBtnHovered ? 180 : 100));
        context.drawText(this.textRenderer, mpLabel, mpBtnX + 6, mpBtnY + 4, mpBtnHovered ? Theme.ACCENT_SECONDARY : Theme.TEXT_SECONDARY, true);

        // ESC hint
        context.drawText(this.textRenderer, hint, guiX + GUI_WIDTH - hintWidth - Theme.PADDING, titleY, Theme.TEXT_MUTED, true);

        // Search bar
        searchBar.render(context, mouseX, mouseY, delta);

        // Category tabs with horizontal scrolling
        int tabY = guiY + Theme.HEADER_HEIGHT + Theme.SEARCH_BAR_HEIGHT + Theme.PADDING * 2;
        int tabAreaLeft = guiX + Theme.PADDING;
        int tabAreaWidth = GUI_WIDTH - Theme.PADDING * 2;
        Category[] categories = Category.values();
        boolean isSearching = !searchBar.getText().isEmpty();

        // Update tab scroll animation
        tabScrollAnim.setTarget(tabScrollTarget);
        tabScrollAnim.update();
        tabScrollOffset = tabScrollAnim.get();

        boolean needsTabScroll = totalTabsWidth > tabAreaWidth;

        // Draw tab scroll arrows if needed
        int arrowWidth = needsTabScroll ? 12 : 0;
        int tabClipLeft = tabAreaLeft + arrowWidth;
        int tabClipRight = tabAreaLeft + tabAreaWidth - arrowWidth;

        if (needsTabScroll) {
            boolean canScrollLeft = tabScrollOffset > 0;
            int leftArrowColor = canScrollLeft ? Theme.TEXT_PRIMARY : Theme.withAlpha(Theme.TEXT_MUTED, 80);
            context.drawText(this.textRenderer, "\u25C0", tabAreaLeft + 1, tabY + (Theme.TAB_HEIGHT - 8) / 2, leftArrowColor, true);

            float maxTabScroll = Math.max(0, totalTabsWidth - (tabClipRight - tabClipLeft));
            boolean canScrollRight = tabScrollOffset < maxTabScroll;
            int rightArrowColor = canScrollRight ? Theme.TEXT_PRIMARY : Theme.withAlpha(Theme.TEXT_MUTED, 80);
            context.drawText(this.textRenderer, "\u25B6", tabAreaLeft + tabAreaWidth - 10, tabY + (Theme.TAB_HEIGHT - 8) / 2, rightArrowColor, true);
        }

        // Scissor for tab area
        enableScissor(context, tabClipLeft, tabY, tabClipRight - tabClipLeft, Theme.TAB_HEIGHT);

        int tabX = tabClipLeft - (int) tabScrollOffset;
        for (int i = 0; i < categories.length; i++) {
            Category cat = categories[i];
            boolean selected = cat == selectedCategory && !isSearching;
            tabAnimations[i].setTarget(selected ? 1f : 0f);
            tabAnimations[i].update();

            int tabWidth = this.textRenderer.getWidth(cat.displayName) + 16;

            boolean tabVisible = tabX + tabWidth > tabClipLeft && tabX < tabClipRight;
            boolean tabHovered = tabVisible && mouseX >= Math.max(tabX, tabClipLeft) && mouseX <= Math.min(tabX + tabWidth, tabClipRight)
                    && mouseY >= tabY && mouseY <= tabY + Theme.TAB_HEIGHT;

            float t = tabAnimations[i].get();
            int tabBg = Theme.lerpColor(Theme.BG_SECONDARY, Theme.BG_CARD_HOVER, tabHovered && !selected ? 0.5f : t * 0.3f);
            GuiRenderUtil.drawRoundedRect(context, tabX, tabY, tabWidth, Theme.TAB_HEIGHT, 3, tabBg);

            int textColor = Theme.lerpColor(Theme.TEXT_MUTED, Theme.ACCENT_PRIMARY, t);
            if (tabHovered && !selected) textColor = Theme.TEXT_PRIMARY;
            context.drawText(this.textRenderer, cat.displayName, tabX + 8, tabY + (Theme.TAB_HEIGHT - 8) / 2, textColor, true);

            if (t > 0.01f) {
                int underlineAlpha = (int) (255 * t);
                int underlineColor = Theme.withAlpha(Theme.ACCENT_PRIMARY, underlineAlpha);
                context.fill(tabX + 2, tabY + Theme.TAB_HEIGHT - 2, tabX + tabWidth - 2, tabY + Theme.TAB_HEIGHT, underlineColor);
                int glowAlpha = (int) (40 * t);
                context.fill(tabX, tabY + Theme.TAB_HEIGHT - 4, tabX + tabWidth, tabY + Theme.TAB_HEIGHT, Theme.withAlpha(Theme.ACCENT_PRIMARY, glowAlpha));
            }

            tabX += tabWidth + 4;
        }

        disableScissor(context);

        // Module list area
        int listTop = tabY + Theme.TAB_HEIGHT + Theme.PADDING;
        int listBottom = guiY + guiHeight - Theme.PADDING;
        int listHeight = listBottom - listTop;

        // Rebuild cards if search/category changed
        List<Module> currentModules = getFilteredModules();
        if (moduleCards.size() != currentModules.size() || !modulesMatch(currentModules)) {
            rebuildCards();
        }

        // Update scroll
        scrollAnim.setTarget(scrollTarget);
        scrollAnim.update();
        float scroll = scrollAnim.get();

        // Calculate content height
        contentHeight = 0;
        for (ModuleCard card : moduleCards) {
            contentHeight += card.getFullHeight() + CARD_SPACING;
        }
        float maxScroll = Math.max(0, contentHeight - listHeight);
        if (scrollTarget > maxScroll) {
            scrollTarget = maxScroll;
            scrollAnim.setTarget(scrollTarget);
        }

        // Render module cards with scissor clipping
        enableScissor(context, guiX, listTop, GUI_WIDTH, listHeight);

        int cardY = listTop - (int) scroll;
        for (ModuleCard card : moduleCards) {
            card.setPosition(card.getX(), cardY);
            int cardH = card.getFullHeight();
            if (cardY + cardH > listTop && cardY < listBottom) {
                card.render(context, mouseX, mouseY, delta);
            }
            cardY += cardH + CARD_SPACING;
        }

        disableScissor(context);

        // Scrollbar
        if (contentHeight > listHeight) {
            int scrollbarX = guiX + GUI_WIDTH - Theme.SCROLLBAR_WIDTH - Theme.PADDING_SMALL;
            float scrollRatio = scroll / Math.max(1, contentHeight - listHeight);
            int scrollbarHeight = Math.max(20, (int) ((float) listHeight / contentHeight * listHeight));
            int scrollbarY = listTop + (int) ((listHeight - scrollbarHeight) * scrollRatio);
            context.fill(scrollbarX, listTop, scrollbarX + Theme.SCROLLBAR_WIDTH, listBottom, Theme.withAlpha(Theme.BG_CARD, 100));
            GuiRenderUtil.drawRoundedRect(context, scrollbarX, scrollbarY, Theme.SCROLLBAR_WIDTH, scrollbarHeight, 1, Theme.ACCENT_PRIMARY);
        }
    }

    private void renderMarketplaceContent(DrawContext context, int mouseX, int mouseY, float delta,
                                          int guiX, int guiY, int guiHeight, int titleY) {
        // Title with glow — same style as main GUI but says "MARKETPLACE"
        String title = "MARKETPLACE";
        int titleX = guiX + Theme.PADDING_LARGE;
        context.drawText(this.textRenderer, title, titleX + 1, titleY, Theme.withAlpha(Theme.ACCENT_PRIMARY, 60), false);
        context.drawText(this.textRenderer, title, titleX, titleY, Theme.ACCENT_PRIMARY, true);

        // Back button in header (right side, before ESC hint)
        String backLabel = "\u2190 Back";
        mpBtnW = this.textRenderer.getWidth(backLabel) + 12;
        mpBtnH = 16;
        String hint = "ESC";
        int hintWidth = this.textRenderer.getWidth(hint);
        mpBtnX = guiX + GUI_WIDTH - hintWidth - Theme.PADDING - mpBtnW - 8;
        mpBtnY = guiY + (Theme.HEADER_HEIGHT - mpBtnH) / 2;

        boolean backHovered = mouseX >= mpBtnX && mouseX <= mpBtnX + mpBtnW
                && mouseY >= mpBtnY && mouseY <= mpBtnY + mpBtnH;
        int backBg = backHovered ? Theme.withAlpha(Theme.ACCENT_SECONDARY, 50) : Theme.withAlpha(Theme.ACCENT_SECONDARY, 25);
        GuiRenderUtil.drawRoundedRect(context, mpBtnX, mpBtnY, mpBtnW, mpBtnH, 3, backBg);
        GuiRenderUtil.drawOutline(context, mpBtnX, mpBtnY, mpBtnW, mpBtnH, Theme.withAlpha(Theme.ACCENT_SECONDARY, backHovered ? 180 : 100));
        context.drawText(this.textRenderer, backLabel, mpBtnX + 6, mpBtnY + 4, backHovered ? Theme.ACCENT_SECONDARY : Theme.TEXT_SECONDARY, true);

        // ESC hint
        context.drawText(this.textRenderer, hint, guiX + GUI_WIDTH - hintWidth - Theme.PADDING, titleY, Theme.TEXT_MUTED, true);

        // Search bar for marketplace
        int searchY = guiY + Theme.HEADER_HEIGHT + Theme.PADDING_SMALL;
        if (marketplaceSearchBar == null) {
            marketplaceSearchBar = new SearchBar(guiX + Theme.PADDING, searchY, GUI_WIDTH - Theme.PADDING * 2);
        } else {
            marketplaceSearchBar.setPosition(guiX + Theme.PADDING, searchY);
            marketplaceSearchBar.setSize(GUI_WIDTH - Theme.PADDING * 2, Theme.SEARCH_BAR_HEIGHT);
        }
        marketplaceSearchBar.render(context, mouseX, mouseY, delta);

        // Marketplace panel content area
        int panelTop = guiY + Theme.HEADER_HEIGHT + Theme.SEARCH_BAR_HEIGHT + Theme.PADDING * 2;
        int panelBottom = guiY + guiHeight - Theme.PADDING;
        int panelHeight = panelBottom - panelTop;
        int panelWidth = GUI_WIDTH - Theme.PADDING * 2 - Theme.SCROLLBAR_WIDTH - 2;

        if (marketplacePanel == null) {
            marketplacePanel = new MarketplacePanel(guiX + Theme.PADDING, panelTop, panelWidth, panelHeight);
            marketplacePanel.refresh();
        } else {
            marketplacePanel.setPosition(guiX + Theme.PADDING, panelTop);
            marketplacePanel.setSize(panelWidth, panelHeight);
        }

        // Forward search filter
        marketplacePanel.setSearchFilter(marketplaceSearchBar.getText().toLowerCase());

        enableScissor(context, guiX, panelTop, GUI_WIDTH, panelHeight);
        marketplacePanel.render(context, mouseX, mouseY, delta);
        disableScissor(context);
    }

    private void enableScissor(DrawContext context, int x, int y, int width, int height) {
        context.enableScissor(x, y, x + width, y + height);
    }

    private void disableScissor(DrawContext context) {
        context.disableScissor();
    }

    private boolean modulesMatch(List<Module> expected) {
        if (moduleCards.size() != expected.size()) return false;
        for (int i = 0; i < expected.size(); i++) {
            if (moduleCards.get(i).getModule() != expected.get(i)) return false;
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // If marketplace view is open, route input only to marketplace UI
        if (marketplaceOpen) {
            if (mouseX >= mpBtnX && mouseX <= mpBtnX + mpBtnW && mouseY >= mpBtnY && mouseY <= mpBtnY + mpBtnH) {
                marketplaceOpen = false;
                return true;
            }

            // Search bar
            if (marketplaceSearchBar != null && marketplaceSearchBar.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }

            // Marketplace panel
            if (marketplacePanel != null && marketplacePanel.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }

            return true;
        }

        // Marketplace button in header
        if (mouseX >= mpBtnX && mouseX <= mpBtnX + mpBtnW && mouseY >= mpBtnY && mouseY <= mpBtnY + mpBtnH) {
            marketplaceOpen = true;
            return true;
        }

        if (searchBar.mouseClicked(mouseX, mouseY, button)) {
            rebuildCards();
            scrollTarget = 0;
            return true;
        }

        // Category tabs
        int guiX = (this.width - GUI_WIDTH) / 2;
        int guiY = GUI_MARGIN_TOP;
        int tabY = guiY + Theme.HEADER_HEIGHT + Theme.SEARCH_BAR_HEIGHT + Theme.PADDING * 2;
        int tabAreaLeft = guiX + Theme.PADDING;
        int tabAreaWidth = GUI_WIDTH - Theme.PADDING * 2;
        boolean needsTabScroll = totalTabsWidth > tabAreaWidth;
        int arrowWidth = needsTabScroll ? 12 : 0;
        int tabClipLeft = tabAreaLeft + arrowWidth;
        int tabClipRight = tabAreaLeft + tabAreaWidth - arrowWidth;

        // Check arrow clicks
        if (needsTabScroll && mouseY >= tabY && mouseY <= tabY + Theme.TAB_HEIGHT) {
            if (mouseX >= tabAreaLeft && mouseX <= tabAreaLeft + arrowWidth) {
                tabScrollTarget = Math.max(0, tabScrollTarget - 60);
                return true;
            }
            if (mouseX >= tabAreaLeft + tabAreaWidth - arrowWidth && mouseX <= tabAreaLeft + tabAreaWidth) {
                float maxTabScroll = Math.max(0, totalTabsWidth - (tabClipRight - tabClipLeft));
                tabScrollTarget = Math.min(maxTabScroll, tabScrollTarget + 60);
                return true;
            }
        }

        // Check tab clicks
        int tabX = tabClipLeft - (int) tabScrollOffset;
        for (Category cat : Category.values()) {
            int tabWidth = this.textRenderer.getWidth(cat.displayName) + 16;
            int visibleLeft = Math.max(tabX, tabClipLeft);
            int visibleRight = Math.min(tabX + tabWidth, tabClipRight);
            if (visibleLeft < visibleRight && mouseX >= visibleLeft && mouseX <= visibleRight
                    && mouseY >= tabY && mouseY <= tabY + Theme.TAB_HEIGHT) {
                selectedCategory = cat;
                persistedCategory = cat;
                scrollTarget = 0;
                persistedScroll = scrollTarget;
                rebuildCards();
                return true;
            }
            tabX += tabWidth + 4;
        }

        for (ModuleCard card : moduleCards) {
            if (card.mouseClicked(mouseX, mouseY, button)) return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (marketplaceOpen) return true;
        for (ModuleCard card : moduleCards) {
            if (card.mouseReleased(mouseX, mouseY, button)) return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (marketplaceOpen) return true;
        for (ModuleCard card : moduleCards) {
            if (card.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (marketplaceOpen) {
            if (marketplacePanel != null) {
                return marketplacePanel.mouseScrolled(mouseX, mouseY, verticalAmount);
            }
            return true;
        }

        int guiX = (this.width - GUI_WIDTH) / 2;
        int guiY = GUI_MARGIN_TOP;
        int tabY = guiY + Theme.HEADER_HEIGHT + Theme.SEARCH_BAR_HEIGHT + Theme.PADDING * 2;
        int tabAreaLeft = guiX + Theme.PADDING;
        int tabAreaWidth = GUI_WIDTH - Theme.PADDING * 2;

        // Horizontal scroll on tabs when hovering over tab area
        if (mouseY >= tabY && mouseY <= tabY + Theme.TAB_HEIGHT
                && mouseX >= tabAreaLeft && mouseX <= tabAreaLeft + tabAreaWidth
                && totalTabsWidth > tabAreaWidth) {
            boolean needsTabScroll = totalTabsWidth > tabAreaWidth;
            int arrowWidth = needsTabScroll ? 12 : 0;
            int tabClipLeft = tabAreaLeft + arrowWidth;
            int tabClipRight = tabAreaLeft + tabAreaWidth - arrowWidth;
            float maxTabScroll = Math.max(0, totalTabsWidth - (tabClipRight - tabClipLeft));
            tabScrollTarget -= (float) verticalAmount * 30;
            tabScrollTarget = Math.max(0, Math.min(maxTabScroll, tabScrollTarget));
            return true;
        }

        // Vertical scroll for module list
        int listTop = tabY + Theme.TAB_HEIGHT + Theme.PADDING;
        int listBottom = guiY + (this.height - guiY * 2) - Theme.PADDING;
        int listHeight = listBottom - listTop;

        float maxScroll = Math.max(0, contentHeight - listHeight);
        scrollTarget -= (float) verticalAmount * 24;
        scrollTarget = Math.max(0, Math.min(maxScroll, scrollTarget));
        persistedScroll = scrollTarget;
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC closes marketplace first, then closes GUI
        if (marketplaceOpen) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                marketplaceOpen = false;
                return true;
            }
            if (marketplaceSearchBar != null && marketplaceSearchBar.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT && isAnyTextInputFocused()) {
            return true;
        }

        if (searchBar.keyPressed(keyCode, scanCode, modifiers)) {
            rebuildCards();
            scrollTarget = 0;
            return true;
        }

        for (ModuleCard card : moduleCards) {
            if (card.keyPressed(keyCode, scanCode, modifiers)) return true;
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            close();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean isAnyTextInputFocused() {
        if (searchBar != null && searchBar.isFocused()) return true;
        for (ModuleCard card : moduleCards) {
            if (card.isTextInputFocused()) return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (marketplaceOpen) {
            if (marketplaceSearchBar != null && marketplaceSearchBar.charTyped(chr, modifiers)) {
                return true;
            }
            return true;
        }

        if (searchBar.charTyped(chr, modifiers)) {
            rebuildCards();
            scrollTarget = 0;
            return true;
        }
        for (ModuleCard card : moduleCards) {
            if (card.charTyped(chr, modifiers)) return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public static JsonObject toStateJson() {
        JsonObject json = new JsonObject();
        json.addProperty("selectedCategory", persistedCategory.name());
        json.addProperty("scroll", persistedScroll);
        return json;
    }

    public static void fromStateJson(JsonObject json) {
        if (json == null) return;

        if (json.has("selectedCategory")) {
            try {
                persistedCategory = Category.valueOf(json.get("selectedCategory").getAsString());
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (json.has("scroll")) {
            persistedScroll = Math.max(0f, json.get("scroll").getAsFloat());
        }
    }
}
