package dev.oblivion.client.module.bots;

import dev.oblivion.client.util.ChatUtil;

public final class BotRawCommand extends BotModule {
    public BotRawCommand() {
        super("BotRawCommand", "Shows current integrated bot runtime status.");
    }

    @Override
    protected void onEnable() {
        int count = botManager().getBotCount();
        ChatUtil.info("Bot status: " + count + " connected/managed bot(s)");
        disable();
    }
}
