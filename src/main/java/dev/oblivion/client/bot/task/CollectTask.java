package dev.oblivion.client.bot.task;

import dev.oblivion.client.bot.Bot;
import dev.oblivion.client.bot.BotWorldTracker;
import org.geysermc.mcprotocollib.protocol.data.game.entity.type.EntityType;

public class CollectTask extends BotTask {
    private final double radius;
    private int tickCounter;

    public CollectTask(double radius) {
        this.radius = radius;
    }

    @Override
    public void tick(Bot bot) {
        if (!isRunning()) return;
        if (++tickCounter % 7 != 0) return; // every 350ms

        BotWorldTracker tracker = bot.getTracker();
        double bx = tracker.getX(), by = tracker.getY(), bz = tracker.getZ();

        BotWorldTracker.TrackedEntity closest = null;
        double closestDist = Double.MAX_VALUE;

        for (BotWorldTracker.TrackedEntity e : tracker.getEntities().values()) {
            if (e.type() != EntityType.ITEM) continue;
            double dx = e.x() - bx, dy = e.y() - by, dz = e.z() - bz;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist <= radius && dist < closestDist) {
                closestDist = dist;
                closest = e;
            }
        }

        if (closest != null) {
            bot.getMovement().setGoal(closest.x(), closest.y(), closest.z(), 0.5);
        }
    }
}
