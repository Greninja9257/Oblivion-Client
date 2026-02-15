package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.StringSetting;
import net.minecraft.client.gui.screen.ChatScreen;

public final class WurstAutoComplete extends Module {
    private final StringSetting suggestion = settings.getDefaultGroup().add(
        new StringSetting.Builder().name("Suggestion").description("Default clipboard suggestion").defaultValue("/msg ").build()
    );

    private boolean primed;

    public WurstAutoComplete() {
        super("AutoComplete", "Preloads a preferred command snippet when chat opens.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        primed = false;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.currentScreen instanceof ChatScreen) {
            if (!primed) {
                mc.keyboard.setClipboard(suggestion.get());
                primed = true;
            }
        } else {
            primed = false;
        }
    }
}
