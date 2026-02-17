package dev.oblivion.client.bot.task;

import dev.oblivion.client.bot.Bot;
import dev.oblivion.client.bot.BotBlockSearch;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.data.game.entity.object.Direction;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.PlayerAction;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundPlayerActionPacket;

public class MineTask extends BotTask {
    private final String blockId;
    private final int maxBlocks;

    private Block targetBlock;
    private BlockPos targetPos;
    private int breakTicks;
    private int mined;

    public MineTask(String blockId, int maxBlocks) {
        this.blockId = blockId;
        this.maxBlocks = Math.max(1, maxBlocks);
    }

    @Override
    protected void onStart(Bot bot) {
        Identifier id = Identifier.tryParse(blockId);
        targetBlock = id == null ? null : Registries.BLOCK.get(id);
        targetPos = null;
        breakTicks = 0;
        mined = 0;
    }

    @Override
    public void tick(Bot bot) {
        if (!isRunning()) return;
        if (targetBlock == null || targetBlock == Registries.BLOCK.get(Identifier.of("minecraft", "air"))) {
            stop(bot);
            return;
        }
        if (mined >= maxBlocks) {
            stop(bot);
            return;
        }

        if (targetPos == null) {
            targetPos = BotBlockSearch.findNearest(
                bot.getTracker().getX(),
                bot.getTracker().getY(),
                bot.getTracker().getZ(),
                24,
                state -> state.isOf(targetBlock)
            );
            if (targetPos == null) {
                stop(bot);
                return;
            }
            bot.getMovement().setGoal(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 1.4);
            return;
        }

        if (!BotBlockSearch.isBlock(targetPos, targetBlock)) {
            targetPos = null;
            breakTicks = 0;
            return;
        }

        double dx = (targetPos.getX() + 0.5) - bot.getTracker().getX();
        double dz = (targetPos.getZ() + 0.5) - bot.getTracker().getZ();
        if (Math.sqrt(dx * dx + dz * dz) > 1.8) {
            bot.getMovement().setGoal(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 1.4);
            return;
        }

        if (breakTicks <= 0) {
            Vector3i pos = Vector3i.from(targetPos.getX(), targetPos.getY(), targetPos.getZ());
            int sequence = bot.nextActionSequence();
            bot.sendPacket(new ServerboundPlayerActionPacket(PlayerAction.START_DIGGING, pos, Direction.UP, sequence));

            BlockState state = BotBlockSearch.getBlockState(targetPos);
            float hardness = 1.5f;
            if (state != null && MinecraftClient.getInstance().world != null) {
                hardness = state.getHardness(MinecraftClient.getInstance().world, targetPos);
            }
            breakTicks = hardness <= 0 ? 6 : Math.max(4, Math.min(120, (int) (hardness * 30)));
            return;
        }

        breakTicks--;
        if (breakTicks == 0) {
            Vector3i pos = Vector3i.from(targetPos.getX(), targetPos.getY(), targetPos.getZ());
            int sequence = bot.nextActionSequence();
            bot.sendPacket(new ServerboundPlayerActionPacket(PlayerAction.FINISH_DIGGING, pos, Direction.UP, sequence));
            mined++;
            targetPos = null;
        }
    }
}
