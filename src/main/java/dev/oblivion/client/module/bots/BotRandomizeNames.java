package dev.oblivion.client.module.bots;

import java.util.ArrayList;
import java.util.List;

public final class BotRandomizeNames extends BotModule {
    private final dev.oblivion.client.setting.impl.StringSetting namePrefix = settings.getDefaultGroup().add(
        new dev.oblivion.client.setting.impl.StringSetting.Builder().name("Name Prefix").description("Prefix used for generated names.").defaultValue("obv_").build()
    );

    private final dev.oblivion.client.setting.impl.IntSetting randomLength = settings.getDefaultGroup().add(
        new dev.oblivion.client.setting.impl.IntSetting.Builder().name("Random Length").description("Suffix length for generated names.").defaultValue(6).range(3, 16).build()
    );

    public BotRandomizeNames() {
        super("BotRandomizeNames", "Generates and applies random bot names for configured amount.");
    }

    @Override
    protected void onEnable() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < botAmount.get(); i++) {
            names.add(botManager().randomName(namePrefix.get(), randomLength.get()));
        }
        botManager().setNames(names);
        reportAction("queued " + names.size() + " generated name(s)");
        disable();
    }
}
