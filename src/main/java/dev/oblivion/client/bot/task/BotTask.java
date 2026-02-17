package dev.oblivion.client.bot.task;

import dev.oblivion.client.bot.Bot;

public abstract class BotTask {
    private volatile boolean running;

    public void start(Bot bot) {
        running = true;
        onStart(bot);
    }

    public void stop(Bot bot) {
        running = false;
        onStop(bot);
    }

    public boolean isRunning() {
        return running;
    }

    public abstract void tick(Bot bot);

    protected void onStart(Bot bot) {}

    protected void onStop(Bot bot) {}
}
