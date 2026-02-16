package dev.oblivion.client.module.misc;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.StringSetting;

public class BetterChat extends Module {

    private final BoolSetting prefix = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Prefix")
            .description("Add a prefix to outgoing messages.")
            .defaultValue(false)
            .build()
    );

    private final StringSetting prefixText = settings.getDefaultGroup().add(
        new StringSetting.Builder()
            .name("Prefix Text")
            .description("Text to prepend to messages.")
            .defaultValue("> ")
            .visible(prefix::get)
            .build()
    );

    private final BoolSetting suffix = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Suffix")
            .description("Add a suffix to outgoing messages.")
            .defaultValue(false)
            .build()
    );

    private final StringSetting suffixText = settings.getDefaultGroup().add(
        new StringSetting.Builder()
            .name("Suffix Text")
            .description("Text to append to messages.")
            .defaultValue(" | Oblivion")
            .visible(suffix::get)
            .build()
    );

    private final BoolSetting antiSpam = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Anti Spam")
            .description("Stack duplicate incoming messages.")
            .defaultValue(true)
            .build()
    );

    public BetterChat() {
        super("BetterChat", "Improves the chat with prefix, suffix, and anti-spam features.", Category.MISC);
    }

    public String modifyOutgoing(String message) {
        if (message.startsWith("/")) return message;
        String result = message;
        if (prefix.get()) result = prefixText.get() + result;
        if (suffix.get()) result = result + suffixText.get();
        return result;
    }

    public boolean shouldAntiSpam() { return antiSpam.get(); }
}
