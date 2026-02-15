package dev.oblivion.client.module.bots;

import com.google.gson.JsonObject;
import dev.oblivion.client.setting.impl.DoubleSetting;
import dev.oblivion.client.setting.impl.StringSetting;

public final class BotFollow extends BotModule {
    private final StringSetting targetPlayer = settings.getDefaultGroup().add(
        new StringSetting.Builder().name("Target Player").description("Player name for bots to follow.").defaultValue("player").build()
    );

    private final DoubleSetting followDistance = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Distance").description("Desired follow distance.").defaultValue(2.5).range(0.5, 16.0).build()
    );

    public BotFollow() {
        super("BotFollow", "Tells bots to follow a player while enabled.");
    }

    @Override
    protected void onEnable() {
        JsonObject payload = createBasePayload("follow_start");
        payload.addProperty("target", targetPlayer.get());
        payload.addProperty("distance", followDistance.get());
        sendAndReport(payload);
    }

    @Override
    protected void onDisable() {
        JsonObject payload = createBasePayload("follow_stop");
        sendAndReport(payload);
    }
}
