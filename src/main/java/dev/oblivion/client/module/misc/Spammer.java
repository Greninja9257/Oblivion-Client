package dev.oblivion.client.module.misc;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.setting.impl.StringSetting;

public class Spammer extends Module {
    private final StringSetting message = settings.getDefaultGroup().add(
        new StringSetting.Builder().name("Message").description("Message to spam.").defaultValue("oblivion").build()
    );
    private final IntSetting delayMs = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Delay").description("Delay in ms.").defaultValue(5000).min(1000).max(60000).build()
    );

    private long lastSend;

    public Spammer() {
        super("Spammer", "Sends repeated chat messages.", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        long now = System.currentTimeMillis();
        if (now - lastSend < delayMs.get()) return;
        if (!message.get().isBlank()) mc.getNetworkHandler().sendChatMessage(message.get());
        lastSend = now;
    }
}
