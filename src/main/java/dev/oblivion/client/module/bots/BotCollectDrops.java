package dev.oblivion.client.module.bots;

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
        botManager().startCollect(botAmount.get(), radius.get());
        reportAction("collect task started");
    }

    @Override
    protected void onDisable() {
        botManager().stopCollect(botAmount.get());
        reportAction("collect task stopped");
    }
}
