package dev.oblivion.client.module.bots;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.setting.impl.StringSetting;

public final class BotRandomizeNames extends BotModule {
    private final StringSetting namePrefix = settings.getDefaultGroup().add(
        new StringSetting.Builder().name("Name Prefix").description("Prefix used for generated names.").defaultValue("obv_").build()
    );

    private final IntSetting randomLength = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Random Length").description("Suffix length for generated names.").defaultValue(6).range(3, 16).build()
    );

    public BotRandomizeNames() {
        super("BotRandomizeNames", "Generates and applies random bot names for configured amount.");
    }

    @Override
    protected void onEnable() {
        JsonObject payload = createBasePayload("set_names");
        JsonArray names = new JsonArray();
        for (int i = 0; i < botAmount.get(); i++) {
            names.add(OblivionClient.get().botBridgeManager.randomName(namePrefix.get(), randomLength.get()));
        }
        payload.add("names", names);
        sendAndReport(payload);
        disable();
    }
}
