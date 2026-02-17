package dev.oblivion.client.bot.task;

import dev.oblivion.client.bot.Bot;
import net.minecraft.util.math.BlockPos;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.data.game.entity.object.Direction;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.ContainerActionType;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.ShiftClickItemAction;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.inventory.ServerboundContainerClickPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.inventory.ServerboundContainerClosePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundUseItemOnPacket;

import java.util.HashMap;

public class DepositTask extends BotTask {
    private final int x, y, z;

    private Phase phase = Phase.MOVE;
    private int timeoutTicks;
    private int nextScanSlot;

    private enum Phase { MOVE, OPENING, DEPOSITING, CLOSING }

    public DepositTask(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    protected void onStart(Bot bot) {
        phase = Phase.MOVE;
        timeoutTicks = 0;
        nextScanSlot = 0;
        bot.getMovement().setGoal(x + 0.5, y, z + 0.5, 2.0);
    }

    @Override
    public void tick(Bot bot) {
        if (!isRunning()) return;

        switch (phase) {
            case MOVE -> tickMove(bot);
            case OPENING -> tickOpening(bot);
            case DEPOSITING -> tickDepositing(bot);
            case CLOSING -> tickClosing(bot);
        }
    }

    private void tickMove(Bot bot) {
        if (!bot.getMovement().hasReachedGoal()) {
            timeoutTicks++;
            if (timeoutTicks > 400) stop(bot);
            return;
        }

        timeoutTicks = 0;
        int seq = bot.nextActionSequence();
        bot.sendPacket(new ServerboundUseItemOnPacket(
            Vector3i.from(x, y, z),
            Direction.UP,
            Hand.MAIN_HAND,
            0.5f,
            0.5f,
            0.5f,
            false,
            false,
            seq
        ));

        phase = Phase.OPENING;
    }

    private void tickOpening(Bot bot) {
        timeoutTicks++;
        if (bot.getInventory().isContainerOpen()) {
            phase = Phase.DEPOSITING;
            timeoutTicks = 0;
            nextScanSlot = bot.getInventory().getOpenContainerSize();
            return;
        }

        if (timeoutTicks == 30) {
            int seq = bot.nextActionSequence();
            bot.sendPacket(new ServerboundUseItemOnPacket(
                Vector3i.from(x, y, z),
                Direction.UP,
                Hand.MAIN_HAND,
                0.5f,
                0.5f,
                0.5f,
                false,
                false,
                seq
            ));
        }

        if (timeoutTicks > 80) {
            stop(bot);
        }
    }

    private void tickDepositing(Bot bot) {
        if (!bot.getInventory().isContainerOpen()) {
            stop(bot);
            return;
        }

        int containerId = bot.getInventory().getOpenContainerId();
        int stateId = bot.getInventory().getOpenContainerStateId();
        int containerSize = bot.getInventory().getOpenContainerSize();
        if (containerSize <= 0) {
            phase = Phase.CLOSING;
            return;
        }

        int playerStart = containerSize;
        int playerEnd = containerSize + 36;

        while (nextScanSlot < playerEnd) {
            int slot = nextScanSlot++;
            ItemStack item = bot.getInventory().getOpenContainerSlots().get(slot);
            if (item == null || item.getAmount() <= 0) continue;

            bot.sendPacket(new ServerboundContainerClickPacket(
                containerId,
                stateId,
                slot,
                ContainerActionType.SHIFT_CLICK_ITEM,
                ShiftClickItemAction.LEFT_CLICK,
                null,
                new HashMap<>()
            ));
            return;
        }

        phase = Phase.CLOSING;
    }

    private void tickClosing(Bot bot) {
        if (bot.getInventory().isContainerOpen()) {
            bot.sendPacket(new ServerboundContainerClosePacket(bot.getInventory().getOpenContainerId()));
        }
        stop(bot);
    }
}
