package dev.oblivion.client.module.bots;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.bot.BotManager;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.util.ChatUtil;

public abstract class BotModule extends Module {

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

    protected BotManager botManager() {
        return OblivionClient.get().botManager;
    }

    protected void reportAction(String action) {
        ChatUtil.success(name + ": " + action);
    }
}
