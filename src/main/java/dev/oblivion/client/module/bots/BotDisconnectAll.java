package dev.oblivion.client.module.bots;

public final class BotDisconnectAll extends BotModule {
    public BotDisconnectAll() {
        super("BotDisconnectAll", "Disconnects all managed bots.");
    }

    @Override
    protected void onEnable() {
        int disconnected = botManager().disconnectAll();
        reportAction("disconnected " + disconnected + " bot(s)");
        disable();
    }
}
