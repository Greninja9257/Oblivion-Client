package dev.oblivion.client.bot.task;

import dev.oblivion.client.bot.Bot;
import dev.oblivion.client.bot.BotWorldTracker;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.InteractAction;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundInteractPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundSwingPacket;

import java.util.Set;

public class GuardTask extends BotTask {
    private final String targetName;
    private int tickCounter;

    private static final Set<String> HOSTILE_TYPES = Set.of(
        "ZOMBIE", "SKELETON", "CREEPER", "SPIDER", "ENDERMAN", "WITCH", "BLAZE", "GHAST",
        "WITHER_SKELETON", "DROWNED", "HUSK", "STRAY", "PHANTOM", "PILLAGER", "VINDICATOR",
        "RAVAGER", "VEX", "EVOKER", "WARDEN", "BREEZE", "BOGGED"
    );

    public GuardTask(String targetName) {
        this.targetName = targetName;
    }

    @Override
    public void tick(Bot bot) {
        if (!isRunning()) return;
        if (++tickCounter % 9 != 0) return;

        BotWorldTracker tracker = bot.getTracker();
        BotWorldTracker.TrackedPlayer player = tracker.getPlayerByName(targetName);
        if (player == null) return;

        BotWorldTracker.TrackedEntity targetEntity = tracker.findEntityByUuid(player.uuid());
        if (targetEntity == null) return;

        BotWorldTracker.TrackedEntity nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (BotWorldTracker.TrackedEntity e : tracker.getEntities().values()) {
            if (!HOSTILE_TYPES.contains(e.type().name())) continue;

            double dx = e.x() - targetEntity.x();
            double dy = e.y() - targetEntity.y();
            double dz = e.z() - targetEntity.z();
            double distToTarget = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distToTarget > 8.0) continue;

            double bx = e.x() - tracker.getX();
            double bz = e.z() - tracker.getZ();
            double distToBot = Math.sqrt(bx * bx + bz * bz);
            if (distToBot < nearestDist) {
                nearestDist = distToBot;
                nearest = e;
            }
        }

        if (nearest != null) {
            bot.getMovement().setGoal(nearest.x(), nearest.y(), nearest.z(), 1.5);
            if (nearestDist <= 4.0) {
                bot.sendPacket(new ServerboundInteractPacket(nearest.entityId(), InteractAction.ATTACK, false));
                bot.sendPacket(new ServerboundSwingPacket(Hand.MAIN_HAND));
            }
        } else {
            bot.getMovement().setGoal(targetEntity.x(), targetEntity.y(), targetEntity.z(), 2.0);
        }
    }
}
