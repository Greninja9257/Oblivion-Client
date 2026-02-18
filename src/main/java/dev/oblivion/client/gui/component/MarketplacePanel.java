package dev.oblivion.client.gui.component;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.addon.AddonManager;
import dev.oblivion.client.addon.marketplace.MarketplaceEntry;
import dev.oblivion.client.addon.marketplace.MarketplaceIndex;
import dev.oblivion.client.gui.animation.Animation;
import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Scrollable marketplace panel with sort buttons, search filtering, and addon cards.
 */
public class MarketplacePanel extends Component {
    public enum SortMode { RECENT, NAME, VOTES }

    private static final String[] SORT_LABELS = {"Recent", "Name", "Votes"};
    private static final int SORT_BAR_HEIGHT = 20;
    private static final int CARD_SPACING = 4;

    private final List<MarketplaceCard> cards = new ArrayList<>();
    private SortMode sortMode = SortMode.RECENT;
    private String searchFilter = "";
    private volatile boolean loading = false;
    private volatile String errorMessage = null;
    private final Animation scrollAnim = new Animation(0f, Theme.ANIM_SPEED_FAST);
    private float scrollTarget = 0;
    private int contentHeight = 0;
    private MarketplaceIndex lastIndex;

    public MarketplacePanel(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void refresh() {
        loading = true;
        errorMessage = null;
        cards.clear();

        OblivionClient.get().addonManager.getMarketplaceClient().fetchIndex().thenAccept(index -> {
            if (index == null) {
                errorMessage = "Failed to load marketplace. Check connection.";
                loading = false;
                return;
            }
            lastIndex = index;
            rebuildCards(index);
            loading = false;
        }).exceptionally(e -> {
            errorMessage = "Error: " + e.getMessage();
            loading = false;
            return null;
        });
    }

    private void rebuildCards(MarketplaceIndex index) {
        cards.clear();
        AddonManager mgr = OblivionClient.get().addonManager;
        String filter = searchFilter.toLowerCase();

        List<MarketplaceEntry> filtered = new ArrayList<>();
        for (MarketplaceEntry e : index.getEntries()) {
            if (!filter.isEmpty()) {
                boolean matches = e.getName().toLowerCase().contains(filter)
                    || e.getDescription().toLowerCase().contains(filter)
                    || e.getAuthor().toLowerCase().contains(filter)
                    || e.getTags().stream().anyMatch(t -> t.toLowerCase().contains(filter));
                if (!matches) continue;
            }
            filtered.add(e);
        }

        Comparator<MarketplaceEntry> cmp = switch (sortMode) {
            case RECENT -> Comparator.comparingLong(MarketplaceEntry::getUpdatedAt).reversed();
            case NAME -> Comparator.comparing(MarketplaceEntry::getName, String.CASE_INSENSITIVE_ORDER);
            case VOTES -> Comparator.comparingInt((MarketplaceEntry e) ->
                mgr.getVoteStore().getScore(e.getId())).reversed();
        };
        filtered.sort(cmp);

        for (MarketplaceEntry entry : filtered) {
            cards.add(new MarketplaceCard(entry, x, 0, width));
        }

        scrollTarget = 0;
    }

    public void setSearchFilter(String filter) {
        if (filter.equals(this.searchFilter)) return;
        this.searchFilter = filter;
        if (lastIndex != null) {
            rebuildCards(lastIndex);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int sortY = y;

        // Sort mode buttons
        int sortBtnX = x;
        for (int i = 0; i < SORT_LABELS.length; i++) {
            SortMode mode = SortMode.values()[i];
            boolean selected = sortMode == mode;
            int btnW = mc.textRenderer.getWidth(SORT_LABELS[i]) + 12;

            boolean btnHovered = mouseX >= sortBtnX && mouseX <= sortBtnX + btnW
                && mouseY >= sortY && mouseY <= sortY + 16;
            int btnColor = selected ? Theme.ACCENT_PRIMARY : (btnHovered ? Theme.BG_CARD_HOVER : Theme.BG_CARD);
            GuiRenderUtil.drawRoundedRect(context, sortBtnX, sortY, btnW, 16, 2, btnColor);
            int textColor = selected ? Theme.TEXT_PRIMARY : Theme.TEXT_MUTED;
            context.drawText(mc.textRenderer, SORT_LABELS[i], sortBtnX + 6, sortY + 4, textColor, true);
            sortBtnX += btnW + 4;
        }

        // Refresh button
        String refreshText = loading ? "Loading..." : "Refresh";
        int refreshW = mc.textRenderer.getWidth(refreshText) + 12;
        int refreshX = x + width - refreshW;
        boolean refreshHovered = mouseX >= refreshX && mouseX <= refreshX + refreshW
            && mouseY >= sortY && mouseY <= sortY + 16;
        int refreshColor = refreshHovered ? Theme.BG_CARD_HOVER : Theme.BG_CARD;
        GuiRenderUtil.drawRoundedRect(context, refreshX, sortY, refreshW, 16, 2, refreshColor);
        context.drawText(mc.textRenderer, refreshText, refreshX + 6, sortY + 4,
            loading ? Theme.TEXT_MUTED : Theme.ACCENT_SECONDARY, true);

        int listTop = sortY + SORT_BAR_HEIGHT + 4;
        int listHeight = height - SORT_BAR_HEIGHT - 4;

        // Loading/error state
        if (loading && cards.isEmpty()) {
            String loadingText = "Loading marketplace...";
            int tw = mc.textRenderer.getWidth(loadingText);
            context.drawText(mc.textRenderer, loadingText, x + (width - tw) / 2, listTop + 20, Theme.TEXT_MUTED, true);
            return;
        }

        if (errorMessage != null && cards.isEmpty()) {
            int tw = mc.textRenderer.getWidth(errorMessage);
            context.drawText(mc.textRenderer, errorMessage, x + (width - tw) / 2, listTop + 20, Theme.NOTIFY_WARNING, true);
            return;
        }

        if (cards.isEmpty()) {
            String emptyText = searchFilter.isEmpty() ? "No addons available" : "No results for '" + searchFilter + "'";
            int tw = mc.textRenderer.getWidth(emptyText);
            context.drawText(mc.textRenderer, emptyText, x + (width - tw) / 2, listTop + 20, Theme.TEXT_MUTED, true);
            return;
        }

        // Update scroll
        scrollAnim.setTarget(scrollTarget);
        scrollAnim.update();
        float scroll = scrollAnim.get();

        // Calculate content height
        contentHeight = 0;
        for (MarketplaceCard card : cards) {
            contentHeight += card.getFullHeight() + CARD_SPACING;
        }
        float maxScroll = Math.max(0, contentHeight - listHeight);
        if (scrollTarget > maxScroll) {
            scrollTarget = maxScroll;
            scrollAnim.setTarget(scrollTarget);
        }

        // Render cards
        int cardY = listTop - (int) scroll;
        int listBottom = listTop + listHeight;
        for (MarketplaceCard card : cards) {
            card.setPosition(card.getX(), cardY);
            int cardH = card.getFullHeight();
            if (cardY + cardH > listTop && cardY < listBottom) {
                card.render(context, mouseX, mouseY, delta);
            }
            cardY += cardH + CARD_SPACING;
        }

        // Scrollbar
        if (contentHeight > listHeight) {
            int scrollbarX = x + width - Theme.SCROLLBAR_WIDTH;
            float scrollRatio = scroll / Math.max(1, contentHeight - listHeight);
            int scrollbarHeight = Math.max(20, (int) ((float) listHeight / contentHeight * listHeight));
            int scrollbarY = listTop + (int) ((listHeight - scrollbarHeight) * scrollRatio);
            context.fill(scrollbarX, listTop, scrollbarX + Theme.SCROLLBAR_WIDTH, listBottom,
                Theme.withAlpha(Theme.BG_CARD, 100));
            GuiRenderUtil.drawRoundedRect(context, scrollbarX, scrollbarY, Theme.SCROLLBAR_WIDTH,
                scrollbarHeight, 1, Theme.ACCENT_SECONDARY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;

        int sortY = y;

        // Sort buttons
        int sortBtnX = x;
        for (int i = 0; i < SORT_LABELS.length; i++) {
            int btnW = mc.textRenderer.getWidth(SORT_LABELS[i]) + 12;
            if (mouseX >= sortBtnX && mouseX <= sortBtnX + btnW
                && mouseY >= sortY && mouseY <= sortY + 16) {
                sortMode = SortMode.values()[i];
                if (lastIndex != null) rebuildCards(lastIndex);
                return true;
            }
            sortBtnX += btnW + 4;
        }

        // Refresh button
        String refreshText = loading ? "Loading..." : "Refresh";
        int refreshW = mc.textRenderer.getWidth(refreshText) + 12;
        int refreshX = x + width - refreshW;
        if (mouseX >= refreshX && mouseX <= refreshX + refreshW
            && mouseY >= sortY && mouseY <= sortY + 16 && !loading) {
            refresh();
            return true;
        }

        // Cards
        for (MarketplaceCard card : cards) {
            if (card.mouseClicked(mouseX, mouseY, button)) return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int listTop = y + SORT_BAR_HEIGHT + 4;
        int listHeight = height - SORT_BAR_HEIGHT - 4;
        float maxScroll = Math.max(0, contentHeight - listHeight);
        scrollTarget -= (float) amount * 24;
        scrollTarget = Math.max(0, Math.min(maxScroll, scrollTarget));
        return true;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        for (MarketplaceCard card : cards) {
            card.setPosition(x, card.getY());
        }
    }
}
