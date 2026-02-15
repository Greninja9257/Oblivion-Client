package dev.oblivion.client.module.bots;

import com.google.gson.JsonObject;
import dev.oblivion.client.setting.impl.StringSetting;

public final class BotGuard extends BotModule {
    private final StringSetting targetPlayer = settings.getDefaultGroup().add(
        new StringSetting.Builder().name("Target").description("Player to guard.").defaultValue("player").build()
    );

    public BotGuard() {
        super("BotGuard", "Starts/stops guard/combat escort behavior.");
    }

    @Override
    protected void onEnable() {
        JsonObject payload = createBasePayload("guard_start");
        payload.addProperty("target", targetPlayer.get());
        sendAndReport(payload);
    }

    @Override
    protected void onDisable() {
        JsonObject payload = createBasePayload("guard_stop");
        sendAndReport(payload);
    }
}
