package dev.oblivion.client.module.bots;

import com.google.gson.JsonObject;
import dev.oblivion.client.setting.impl.StringSetting;

public final class BotRawCommand extends BotModule {
    private final StringSetting command = settings.getDefaultGroup().add(
        new StringSetting.Builder().name("Command").description("Raw bridge command name.").defaultValue("status").build()
    );

    private final StringSetting argument = settings.getDefaultGroup().add(
        new StringSetting.Builder().name("Arg").description("Optional raw argument payload.").defaultValue("").build()
    );

    public BotRawCommand() {
        super("BotRawCommand", "Sends a raw custom command to the Mineflayer bridge.");
    }

    @Override
    protected void onEnable() {
        JsonObject payload = createBasePayload(command.get());
        payload.addProperty("arg", argument.get());
        sendAndReport(payload);
        disable();
    }
}
