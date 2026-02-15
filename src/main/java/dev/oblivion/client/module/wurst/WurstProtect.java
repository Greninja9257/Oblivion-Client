package dev.oblivion.client.module.wurst;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;

public final class WurstProtect extends Module {
    private final DoubleSetting health = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Health").description("Defensive trigger HP").defaultValue(10).range(1, 36).build()
    );

    public WurstProtect() {
        super("Protect", "Enables defensive modules on low health.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() > health.get()) return;

        var totem = OblivionClient.get().moduleManager.get("AutoTotem");
        if (totem != null && !totem.isEnabled()) totem.enable();
        var reconnect = OblivionClient.get().moduleManager.get("AutoReconnect");
        if (reconnect != null && !reconnect.isEnabled()) reconnect.enable();
    }
}
