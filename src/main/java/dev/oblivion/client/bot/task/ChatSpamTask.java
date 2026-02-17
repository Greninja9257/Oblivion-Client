package dev.oblivion.client.bot.task;

import dev.oblivion.client.bot.Bot;

public class ChatSpamTask extends BotTask {
    private final String message;
    private final int delayTicks;
    private int counter;

    public ChatSpamTask(String message, int delayMs) {
        this.message = message;
        this.delayTicks = Math.max(5, delayMs / 50);
    }

    @Override
    public void tick(Bot bot) {
        if (!isRunning()) return;
        if (++counter >= delayTicks) {
            counter = 0;
            bot.chat(message);
        }
    }
}
