package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstChatTranslator extends Module {
    public WurstChatTranslator() {
        super("ChatTranslator", "Shows live translation helper hint in HUD.", Category.MISC);
    }

    @EventHandler
    public void onHud(RenderEvent.Hud event) {
        String hint = "Translator: Copy chat -> external translate";
        int x = 8;
        int y = event.getContext().getScaledWindowHeight() - 20;
        event.getContext().drawText(mc.textRenderer, hint, x, y, 0xFF8FD4FF, true);
    }
}
