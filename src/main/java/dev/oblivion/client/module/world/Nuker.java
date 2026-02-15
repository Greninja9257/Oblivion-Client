package dev.oblivion.client.module.world;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import dev.oblivion.client.setting.impl.EnumSetting;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Nuker extends Module {

    public enum Mode { ALL, FLAT, SMASH }

    private final DoubleSetting range = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Range")
            .description("Block breaking range.")
            .defaultValue(4.5)
            .range(1.0, 6.0)
            .build()
    );

    private final EnumSetting<Mode> mode = settings.getDefaultGroup().add(
        new EnumSetting.Builder<Mode>()
            .name("Mode")
            .description("ALL = break everything, FLAT = only same Y and above, SMASH = instant break.")
            .defaultValue(Mode.ALL)
            .build()
    );

    public Nuker() {
        super("Nuker", "Breaks blocks around you automatically.", Category.WORLD);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        double r = range.get();
        BlockPos playerPos = mc.player.getBlockPos();
        int radius = (int) Math.ceil(r);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);

                    if (mc.player.getPos().distanceTo(pos.toCenterPos()) > r) continue;

                    BlockState state = mc.world.getBlockState(pos);
                    if (state.isAir() || state.getBlock() == Blocks.BEDROCK) continue;

                    switch (mode.get()) {
                        case FLAT -> {
                            // Only break blocks at the player's Y level or above
                            if (pos.getY() < playerPos.getY()) continue;
                        }
                        case SMASH -> {
                            // Only break blocks that can be instantly mined
                            if (state.getHardness(mc.world, pos) > 0) continue;
                        }
                        case ALL -> {}
                    }

                    // Send start and stop break packets (instant break)
                    mc.player.networkHandler.sendPacket(
                        new PlayerActionC2SPacket(
                            PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
                            pos,
                            Direction.UP
                        )
                    );
                    mc.player.networkHandler.sendPacket(
                        new PlayerActionC2SPacket(
                            PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                            pos,
                            Direction.UP
                        )
                    );
                }
            }
        }
    }
}
