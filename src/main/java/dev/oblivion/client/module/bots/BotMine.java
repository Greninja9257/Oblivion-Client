package dev.oblivion.client.module.bots;

import com.google.gson.JsonObject;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.setting.impl.StringSetting;

public final class BotMine extends BotModule {
    private final StringSetting blockId = settings.getDefaultGroup().add(
        new StringSetting.Builder().name("Block ID").description("Block id to mine (e.g. minecraft:stone). ").defaultValue("minecraft:stone").build()
    );

    private final IntSetting maxBlocks = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Max Blocks").description("Maximum blocks per bot.").defaultValue(128).range(1, 10000).build()
    );

    public BotMine() {
        super("BotMine", "Starts/stops mining task on bots.");
    }

    @Override
    protected void onEnable() {
        JsonObject payload = createBasePayload("mine_start");
        payload.addProperty("block", blockId.get());
        payload.addProperty("maxBlocks", maxBlocks.get());
        sendAndReport(payload);
    }

    @Override
    protected void onDisable() {
        JsonObject payload = createBasePayload("mine_stop");
        sendAndReport(payload);
    }
}
