package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;

public final class WurstInfiniChat extends Module {
    private final IntSetting length = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Length").description("Message length to send").defaultValue(256).range(32, 500).build()
    );

    private boolean sent;

    public WurstInfiniChat() {
        super("InfiniChat", "Sends a long chat message once per toggle.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        sent = false;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || sent) return;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length.get(); i++) sb.append((char) ('a' + (i % 26)));
        mc.player.networkHandler.sendChatMessage(sb.toString());
        sent = true;
        disable();
    }
}
