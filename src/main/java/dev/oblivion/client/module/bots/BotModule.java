package dev.oblivion.client.module.bots;

import com.google.gson.JsonObject;
import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.bot.BotBridgeManager;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.setting.impl.StringSetting;
import dev.oblivion.client.util.ChatUtil;

public abstract class BotModule extends Module {
    protected final StringSetting bridgeEndpoint = settings.getDefaultGroup().add(
        new StringSetting.Builder()
            .name("Bridge Endpoint")
            .description("Mineflayer bridge HTTP endpoint.")
            .defaultValue(BotBridgeManager.DEFAULT_ENDPOINT)
            .build()
    );

    protected final StringSetting apiToken = settings.getDefaultGroup().add(
        new StringSetting.Builder()
            .name("API Token")
            .description("Optional bearer token for the bot bridge.")
            .defaultValue("")
            .build()
    );

    protected final IntSetting botAmount = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Bot Amount")
            .description("How many bots this module command targets.")
            .defaultValue(1)
            .range(1, 200)
            .build()
    );

    protected BotModule(String name, String description) {
        super(name, description, Category.BOTS);
    }

    protected JsonObject createBasePayload(String action) {
        JsonObject payload = new JsonObject();
        payload.addProperty("action", action);
        payload.addProperty("count", botAmount.get());
        return payload;
    }

    protected void sendAndReport(JsonObject payload) {
        boolean ok = OblivionClient.get().botBridgeManager.sendCommand(bridgeEndpoint.get(), apiToken.get(), payload);
        if (ok) {
            ChatUtil.success(name + " command sent.");
        } else {
            ChatUtil.error(name + " command failed. Check bot bridge endpoint/API token.");
        }
    }
}
