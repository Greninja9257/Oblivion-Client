package dev.oblivion.client.module.bots;

import dev.oblivion.client.setting.impl.StringSetting;

public final class BotGuard extends BotModule {
    private final StringSetting targetPlayer = settings.getDefaultGroup().add(
        new StringSetting.Builder().name("Target").description("Player to guard.").defaultValue("player").build()
    );

    public BotGuard() {
        super("BotGuard", "Starts/stops guard/combat escort behavior.");
    }

    @Override
    protected void onEnable() {
        botManager().startGuard(botAmount.get(), targetPlayer.get());
        reportAction("guard task started");
    }

    @Override
    protected void onDisable() {
        botManager().stopGuard(botAmount.get());
        reportAction("guard task stopped");
    }
}
