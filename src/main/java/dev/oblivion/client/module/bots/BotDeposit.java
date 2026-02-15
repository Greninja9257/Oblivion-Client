package dev.oblivion.client.module.bots;

import com.google.gson.JsonObject;
import dev.oblivion.client.setting.impl.IntSetting;

public final class BotDeposit extends BotModule {
    private final IntSetting chestX = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Chest X").description("Chest X coordinate.").defaultValue(0).range(-30000000, 30000000).build()
    );

    private final IntSetting chestY = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Chest Y").description("Chest Y coordinate.").defaultValue(64).range(-64, 320).build()
    );

    private final IntSetting chestZ = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Chest Z").description("Chest Z coordinate.").defaultValue(0).range(-30000000, 30000000).build()
    );

    public BotDeposit() {
        super("BotDeposit", "Deposits inventory into configured chest coordinates.");
    }

    @Override
    protected void onEnable() {
        JsonObject payload = createBasePayload("deposit");
        payload.addProperty("x", chestX.get());
        payload.addProperty("y", chestY.get());
        payload.addProperty("z", chestZ.get());
        sendAndReport(payload);
        disable();
    }
}
