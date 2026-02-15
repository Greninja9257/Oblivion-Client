package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.RenderEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.StringSetting;

public final class WurstNameProtect extends Module {
    private final StringSetting alias = settings.getDefaultGroup().add(
        new StringSetting.Builder().name("Alias").description("Displayed protected name").defaultValue("ProtectedPlayer").build()
    );

    public WurstNameProtect() {
        super("NameProtect", "Hides your real name in custom HUD indicator.", Category.RENDER);
    }

    @EventHandler
    public void onHud(RenderEvent.Hud event) {
        if (mc.player == null) return;

        String text = "NameProtect: " + alias.get();
        int x = Math.max(8, event.getContext().getScaledWindowWidth() - mc.textRenderer.getWidth(text) - 8);
        int y = 8;
        event.getContext().drawText(mc.textRenderer, text, x, y, 0xFF6AE3FF, true);
    }

    public String getProtectedName() {
        return alias.get();
    }
}
