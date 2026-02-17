package dev.oblivion.client.module.bots;

public final class BotStopTasks extends BotModule {
    public BotStopTasks() {
        super("BotStopTasks", "Stops current scripted tasks for selected bots.");
    }

    @Override
    protected void onEnable() {
        botManager().stopAllTasks(botAmount.get());
        reportAction("tasks stopped");
        disable();
    }
}
