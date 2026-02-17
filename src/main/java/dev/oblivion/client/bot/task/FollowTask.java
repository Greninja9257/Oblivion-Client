package dev.oblivion.client.bot.task;

import dev.oblivion.client.bot.Bot;
import dev.oblivion.client.bot.BotWorldTracker;

public class FollowTask extends BotTask {
    private final String targetName;
    private final double distance;
    private int tickCounter;

    public FollowTask(String targetName, double distance) {
        this.targetName = targetName;
        this.distance = distance;
    }

    @Override
    public void tick(Bot bot) {
        if (!isRunning()) return;
        if (++tickCounter % 10 != 0) return; // every 500ms

        BotWorldTracker tracker = bot.getTracker();
        BotWorldTracker.TrackedPlayer player = tracker.getPlayerByName(targetName);
        if (player == null) return;

        BotWorldTracker.TrackedEntity entity = tracker.findEntityByUuid(player.uuid());
        if (entity == null) return;

        bot.getMovement().setGoal(entity.x(), entity.y(), entity.z(), distance);
    }
}
