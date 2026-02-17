package dev.oblivion.client.module.bots;

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
        botManager().startFarm(botAmount.get(), crop.get());
        reportAction("farm task started");
    }

    @Override
    protected void onDisable() {
        botManager().stopFarm(botAmount.get());
        reportAction("farm task stopped");
    }
}
