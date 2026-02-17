package dev.oblivion.client.module.bots;

import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.setting.impl.StringSetting;

public final class BotSendChat extends BotModule {
    private final StringSetting message = settings.getDefaultGroup().add(
        new StringSetting.Builder()
            .name("Message")
            .description("Chat message for selected bots to send.")
            .defaultValue("hello")
            .build()
    );

    private final BoolSetting repeat = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Repeat")
            .description("Keep sending the message while enabled.")
            .defaultValue(false)
            .build()
    );

    private final IntSetting delayMs = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Delay MS")
            .description("Delay between repeated messages.")
            .defaultValue(1000)
            .range(250, 30000)
            .visible(repeat::get)
            .build()
    );

    public BotSendChat() {
        super("BotSendChat", "Sends chat messages from selected bots.");
    }

    @Override
    protected void onEnable() {
        if (repeat.get()) {
            botManager().chatSpamStart(botAmount.get(), message.get(), delayMs.get());
            reportAction("chat spam started");
        } else {
            botManager().chatSend(botAmount.get(), message.get());
            reportAction("chat sent");
            disable();
        }
    }

    @Override
    protected void onDisable() {
        if (!repeat.get()) return;
        botManager().chatSpamStop(botAmount.get());
        reportAction("chat spam stopped");
    }
}
