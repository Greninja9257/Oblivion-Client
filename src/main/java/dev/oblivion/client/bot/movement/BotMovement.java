package dev.oblivion.client.bot.movement;

import dev.oblivion.client.bot.Bot;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerPosPacket;

public class BotMovement {
    private final Bot bot;
    private volatile double goalX, goalY, goalZ;
    private volatile double goalRadius = 1.0;
    private volatile boolean active = false;

    private static final double WALK_SPEED = 0.216; // blocks per tick

    public BotMovement(Bot bot) {
        this.bot = bot;
    }

    public void setGoal(double x, double y, double z, double radius) {
        this.goalX = x;
        this.goalY = y;
        this.goalZ = z;
        this.goalRadius = radius;
        this.active = true;
    }

    public void clearGoal() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }

    public boolean hasReachedGoal() {
        double dx = goalX - bot.getTracker().getX();
        double dz = goalZ - bot.getTracker().getZ();
        return Math.sqrt(dx * dx + dz * dz) <= goalRadius;
    }

    public void tick() {
        if (!active) return;

        double dx = goalX - bot.getTracker().getX();
        double dz = goalZ - bot.getTracker().getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);

        if (dist <= goalRadius) {
            active = false;
            return;
        }

        double ratio = Math.min(1.0, WALK_SPEED / dist);
        double newX = bot.getTracker().getX() + dx * ratio;
        double newZ = bot.getTracker().getZ() + dz * ratio;

        bot.sendPacket(new ServerboundMovePlayerPosPacket(true, false, newX, goalY, newZ));
    }
}
