package dev.oblivion.client.gui.component;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.addon.AddonManager;
import dev.oblivion.client.addon.marketplace.MarketplaceEntry;
import dev.oblivion.client.addon.marketplace.VoteStore;
import dev.oblivion.client.gui.animation.Animation;
import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import net.minecraft.client.gui.DrawContext;

public class MarketplaceCard extends Component {
    private static final int CARD_HEIGHT = 48;

    private final MarketplaceEntry entry;
    private final Animation hoverAnim = new Animation(0f, Theme.ANIM_SPEED_FAST);
    private volatile boolean installing = false;
    private volatile String installError = null;

    public MarketplaceCard(MarketplaceEntry entry, int x, int y, int width) {
        this.entry = entry;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = CARD_HEIGHT;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hoverAnim.setTarget(isHovered(mouseX, mouseY) ? 1f : 0f);
        hoverAnim.update();

        AddonManager mgr = OblivionClient.get().addonManager;
        boolean installed = mgr.isInstalled(entry.getId());
        VoteStore.Vote myVote = mgr.getVoteStore().getVote(entry.getId());

        // Card background
        int bg = Theme.lerpColor(Theme.BG_CARD, Theme.BG_CARD_HOVER, hoverAnim.get());
        GuiRenderUtil.drawRoundedRect(context, x, y, width, CARD_HEIGHT, 4, bg);

        // Installed accent bar (green left bar)
        if (installed) {
            context.fill(x, y + 4, x + 3, y + CARD_HEIGHT - 4, Theme.ACCENT_ENABLED);
        }

        // Name
        int textLeft = x + 10;
        context.drawText(mc.textRenderer, entry.getName(), textLeft, y + 6, Theme.TEXT_PRIMARY, true);

        // Author
        int nameWidth = mc.textRenderer.getWidth(entry.getName());
        String authorStr = "by " + entry.getAuthor();
        context.drawText(mc.textRenderer, authorStr, textLeft + nameWidth + 8, y + 6, Theme.TEXT_MUTED, true);

        // Description (second line, truncated)
        String desc = entry.getDescription();
        int maxDescWidth = width - 120;
        if (mc.textRenderer.getWidth(desc) > maxDescWidth) {
            desc = mc.textRenderer.trimToWidth(desc, maxDescWidth - 10) + "...";
        }
        context.drawText(mc.textRenderer, desc, textLeft, y + 20, Theme.TEXT_SECONDARY, true);

        // Version/status text (bottom right)
        String ver = "v" + entry.getVersion();
        String status = installed ? "Installed" : null;
        if (status != null) {
            int statusW = mc.textRenderer.getWidth(status);
            context.drawText(mc.textRenderer, status, x + width - statusW - 8, y + 34, Theme.ACCENT_ENABLED, true);
        } else {
            int verW = mc.textRenderer.getWidth(ver);
            context.drawText(mc.textRenderer, ver, x + width - verW - 8, y + 34, Theme.TEXT_MUTED, true);
        }

        // Vote buttons (bottom left)
        int voteY = y + 33;
        int thumbUpX = textLeft;
        int thumbUpColor = myVote == VoteStore.Vote.UP ? Theme.ACCENT_ENABLED : Theme.TEXT_MUTED;
        context.drawText(mc.textRenderer, "\u25B2", thumbUpX, voteY, thumbUpColor, true);

        int thumbDownX = thumbUpX + 16;
        int thumbDownColor = myVote == VoteStore.Vote.DOWN ? Theme.NOTIFY_DISABLED : Theme.TEXT_MUTED;
        context.drawText(mc.textRenderer, "\u25BC", thumbDownX, voteY, thumbDownColor, true);

        // Category badge (small pill)
        String catStr = entry.getCategory();
        int catW = mc.textRenderer.getWidth(catStr) + 8;
        int btnW = getBtnWidth(installed);
        int catX = x + width - btnW - 8 - catW - 8;
        GuiRenderUtil.drawRoundedRect(context, catX, y + 4, catW, 14, 2, Theme.withAlpha(Theme.ACCENT_PRIMARY, 60));
        context.drawText(mc.textRenderer, catStr, catX + 4, y + 7, Theme.ACCENT_PRIMARY, true);

        // Install/Uninstall button
        String btnText = installing ? "..." : (installed ? "Remove" : "Install");
        int btnX = x + width - btnW - 8;
        int btnY = y + (CARD_HEIGHT - 16) / 2;
        int btnColor = installed ? 0xE0401020 : 0xE0103020;
        int btnBorder = installed ? Theme.NOTIFY_DISABLED : Theme.ACCENT_ENABLED;

        boolean btnHovered = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + 16;
        if (btnHovered) btnColor = installed ? 0xE0602030 : 0xE0205030;

        GuiRenderUtil.drawRoundedRect(context, btnX, btnY, btnW, 16, 3, btnColor);
        GuiRenderUtil.drawOutline(context, btnX, btnY, btnW, 16, Theme.withAlpha(btnBorder, 120));
        int btnTextColor = installed ? Theme.NOTIFY_DISABLED : Theme.ACCENT_ENABLED;
        context.drawText(mc.textRenderer, btnText, btnX + 8, btnY + 4, btnTextColor, true);

        if (installError != null && !installError.isEmpty()) {
            String errText = mc.textRenderer.trimToWidth("Install failed: " + installError, width - 20);
            context.drawText(mc.textRenderer, errText, textLeft, y + 34, Theme.NOTIFY_WARNING, true);
        }
    }

    private int getBtnWidth(boolean installed) {
        String btnText = installing ? "..." : (installed ? "Remove" : "Install");
        return mc.textRenderer.getWidth(btnText) + 16;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isHovered((int) mouseX, (int) mouseY) || button != 0) return false;

        AddonManager mgr = OblivionClient.get().addonManager;
        boolean installed = mgr.isInstalled(entry.getId());

        // Vote up button
        int voteY = y + 33;
        int thumbUpX = x + 10;
        if (mouseX >= thumbUpX && mouseX <= thumbUpX + 12 && mouseY >= voteY && mouseY <= voteY + 10) {
            VoteStore.Vote current = mgr.getVoteStore().getVote(entry.getId());
            mgr.getVoteStore().setVote(entry.getId(),
                current == VoteStore.Vote.UP ? VoteStore.Vote.NONE : VoteStore.Vote.UP);
            return true;
        }

        // Vote down button
        int thumbDownX = thumbUpX + 16;
        if (mouseX >= thumbDownX && mouseX <= thumbDownX + 12 && mouseY >= voteY && mouseY <= voteY + 10) {
            VoteStore.Vote current = mgr.getVoteStore().getVote(entry.getId());
            mgr.getVoteStore().setVote(entry.getId(),
                current == VoteStore.Vote.DOWN ? VoteStore.Vote.NONE : VoteStore.Vote.DOWN);
            return true;
        }

        // Install/Uninstall button
        int btnW = getBtnWidth(installed);
        int btnX = x + width - btnW - 8;
        int btnY = y + (CARD_HEIGHT - 16) / 2;
        if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + 16) {
            if (!installing) {
                installing = true;
                installError = null;
                if (installed) {
                    mgr.uninstallAddon(entry.getId());
                    installing = false;
                } else {
                    mgr.installAddon(entry).whenComplete((v, ex) -> {
                        installing = false;
                        if (ex != null) {
                            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                            String msg = cause.getMessage();
                            installError = msg == null ? cause.getClass().getSimpleName() : msg;
                        } else {
                            installError = null;
                        }
                    });
                }
            }
            return true;
        }

        return false;
    }

    public int getFullHeight() { return CARD_HEIGHT; }
    public MarketplaceEntry getEntry() { return entry; }
}
