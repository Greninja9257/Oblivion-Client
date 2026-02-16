package dev.oblivion.client.module.misc;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.StringSetting;

public class NameProtect extends Module {

    private final StringSetting fakeName = settings.getDefaultGroup().add(
        new StringSetting.Builder()
            .name("Fake Name")
            .description("Name to display instead of yours.")
            .defaultValue("Player")
            .build()
    );

    public NameProtect() {
        super("NameProtect", "Hides your real name in chat and nametags.", Category.MISC);
    }

    public String getFakeName() { return fakeName.get(); }

    public String replaceName(String text) {
        if (mc.player == null) return text;
        String realName = mc.player.getName().getString();
        return text.replace(realName, fakeName.get());
    }
}
