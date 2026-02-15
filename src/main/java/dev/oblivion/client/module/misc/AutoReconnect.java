package dev.oblivion.client.module.misc;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;

public class AutoReconnect extends Module {
    private final IntSetting delayMs = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Delay").description("Reconnect delay in ms.").defaultValue(5000).min(1000).max(30000).build()
    );

    public AutoReconnect() {
        super("AutoReconnect", "Automatically reconnects after disconnect.", Category.MISC);
    }

    public int getDelayMs() {
        return delayMs.get();
    }
}
