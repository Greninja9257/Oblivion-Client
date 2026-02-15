package dev.oblivion.client.module.bots;

import com.google.gson.JsonObject;

public final class BotStopTasks extends BotModule {
    public BotStopTasks() {
        super("BotStopTasks", "Stops current scripted tasks for selected bots.");
    }

    @Override
    protected void onEnable() {
        JsonObject payload = createBasePayload("stop_tasks");
        sendAndReport(payload);
        disable();
    }
}
