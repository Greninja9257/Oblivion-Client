package dev.oblivion.client.module.bots;

import com.google.gson.JsonObject;
import dev.oblivion.client.setting.impl.StringSetting;

public final class BotFarm extends BotModule {
    private final StringSetting crop = settings.getDefaultGroup().add(
        new StringSetting.Builder().name("Crop").description("Crop id to farm (e.g. wheat). ").defaultValue("wheat").build()
    );

    public BotFarm() {
        super("BotFarm", "Starts/stops farming behavior on bots.");
    }

    @Override
    protected void onEnable() {
        JsonObject payload = createBasePayload("farm_start");
        payload.addProperty("crop", crop.get());
        sendAndReport(payload);
    }

    @Override
    protected void onDisable() {
        JsonObject payload = createBasePayload("farm_stop");
        sendAndReport(payload);
    }
}
