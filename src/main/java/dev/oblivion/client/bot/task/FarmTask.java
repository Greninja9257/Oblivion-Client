package dev.oblivion.client.bot.task;

import dev.oblivion.client.bot.Bot;
import dev.oblivion.client.bot.BotBlockSearch;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.data.game.entity.object.Direction;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.PlayerAction;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundPlayerActionPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundUseItemOnPacket;

public class FarmTask extends BotTask {
    private final String cropName;

    private BlockPos targetPos;
    private int breakTicks;
    private BlockPos replantPos;
    private int replantDelay;

    public FarmTask(String cropName) {
        this.cropName = cropName == null ? "wheat" : cropName.toLowerCase();
    }

    @Override
    protected void onStart(Bot bot) {
        targetPos = null;
        breakTicks = 0;
        replantPos = null;
        replantDelay = 0;
    }

    @Override
    public void tick(Bot bot) {
        if (!isRunning()) return;

        if (replantPos != null) {
            if (replantDelay > 0) {
                replantDelay--;
                return;
            }
            tryReplant(bot, replantPos);
            replantPos = null;
        }

        if (targetPos == null) {
            targetPos = BotBlockSearch.findNearest(
                bot.getTracker().getX(),
                bot.getTracker().getY(),
                bot.getTracker().getZ(),
                16,
                this::isTargetCrop
            );
            if (targetPos == null) return;
            bot.getMovement().setGoal(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 1.5);
            return;
        }

        BlockState state = BotBlockSearch.getBlockState(targetPos);
        if (state == null || !isTargetCrop(state)) {
            targetPos = null;
            breakTicks = 0;
            return;
        }

        double dx = (targetPos.getX() + 0.5) - bot.getTracker().getX();
        double dz = (targetPos.getZ() + 0.5) - bot.getTracker().getZ();
        if (Math.sqrt(dx * dx + dz * dz) > 1.9) {
            bot.getMovement().setGoal(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 1.5);
            return;
        }

        if (breakTicks <= 0) {
            Vector3i pos = Vector3i.from(targetPos.getX(), targetPos.getY(), targetPos.getZ());
            int sequence = bot.nextActionSequence();
            bot.sendPacket(new ServerboundPlayerActionPacket(PlayerAction.START_DIGGING, pos, Direction.UP, sequence));
            breakTicks = 8;
            return;
        }

        breakTicks--;
        if (breakTicks == 0) {
            Vector3i pos = Vector3i.from(targetPos.getX(), targetPos.getY(), targetPos.getZ());
            int sequence = bot.nextActionSequence();
            bot.sendPacket(new ServerboundPlayerActionPacket(PlayerAction.FINISH_DIGGING, pos, Direction.UP, sequence));
            replantPos = targetPos;
            replantDelay = 6;
            targetPos = null;
        }
    }

    private void tryReplant(Bot bot, BlockPos cropPos) {
        BlockPos farmlandPos = cropPos.down();
        BlockState below = BotBlockSearch.getBlockState(farmlandPos);
        if (below == null || !(below.getBlock() instanceof FarmlandBlock)) return;

        int sequence = bot.nextActionSequence();
        bot.sendPacket(new ServerboundUseItemOnPacket(
            Vector3i.from(farmlandPos.getX(), farmlandPos.getY(), farmlandPos.getZ()),
            Direction.UP,
            Hand.MAIN_HAND,
            0.5f,
            1.0f,
            0.5f,
            false,
            false,
            sequence
        ));
    }

    private boolean isTargetCrop(BlockState state) {
        String path = Registries.BLOCK.getId(state.getBlock()).getPath().toLowerCase();
        if (!path.contains(cropName) && !("wheat".equals(cropName) && path.contains("wheat"))) {
            return false;
        }

        if (state.getBlock() instanceof CropBlock crop) {
            return crop.isMature(state);
        }

        for (Property<?> property : state.getProperties()) {
            if (property instanceof IntProperty intProperty && "age".equals(intProperty.getName())) {
                Integer value = state.get(intProperty);
                int max = intProperty.getValues().stream().max(Integer::compareTo).orElse(0);
                return value != null && value >= max;
            }
        }

        return true;
    }
}
