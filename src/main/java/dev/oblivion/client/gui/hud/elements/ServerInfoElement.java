package dev.oblivion.client.gui.hud.elements;

import dev.oblivion.client.gui.hud.HudElement;
import dev.oblivion.client.gui.render.GuiRenderUtil;
import dev.oblivion.client.gui.theme.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ServerInfo;

public class ServerInfoElement extends HudElement {

    public ServerInfoElement() {
        super("ServerInfo", 4, 74);
    }

    @Override
    public void render(DrawContext context) {
        if (mc.player == null) return;

        String serverText;
        ServerInfo info = mc.getCurrentServerEntry();
        if (info != null) {
            serverText = info.address;
        } else if (mc.isInSingleplayer()) {
            serverText = "Singleplayer";
        } else {
            serverText = "Unknown";
        }

        int ping = mc.getNetworkHandler() != null && mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()) != null
                ? mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency()
                : -1;

        String pingText = ping >= 0 ? ping + "ms" : "N/A";
        String display = serverText + " | " + pingText;

        width = mc.textRenderer.getWidth(display) + 10;
        height = 14;

        GuiRenderUtil.drawRoundedRect(context, x, y, width, height, 3, Theme.withAlpha(Theme.BG_PANEL, 160));
        context.drawText(mc.textRenderer, display, x + 5, y + 3, Theme.TEXT_SECONDARY, true);
    }
}
