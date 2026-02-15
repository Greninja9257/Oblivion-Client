package dev.oblivion.client.module.bots;

import com.google.gson.JsonObject;
import dev.oblivion.client.setting.impl.DoubleSetting;

public final class BotCollectDrops extends BotModule {
    private final DoubleSetting radius = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Radius").description("Pickup radius for dropped items.").defaultValue(10.0).range(1.0, 64.0).build()
    );

    public BotCollectDrops() {
        super("BotCollectDrops", "Starts/stops item collection mode.");
    }

    @Override
    protected void onEnable() {
        JsonObject payload = createBasePayload("collect_start");
        payload.addProperty("radius", radius.get());
        sendAndReport(payload);
    }

    @Override
    protected void onDisable() {
        JsonObject payload = createBasePayload("collect_stop");
        sendAndReport(payload);
    }
}
