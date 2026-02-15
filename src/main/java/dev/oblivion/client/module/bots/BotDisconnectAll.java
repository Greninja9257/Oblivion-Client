package dev.oblivion.client.module.bots;

import com.google.gson.JsonObject;

public final class BotDisconnectAll extends BotModule {
    public BotDisconnectAll() {
        super("BotDisconnectAll", "Disconnects all managed Mineflayer bots.");
    }

    @Override
    protected void onEnable() {
        JsonObject payload = createBasePayload("disconnect_all");
        sendAndReport(payload);
        disable();
    }
}
