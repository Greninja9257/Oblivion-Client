package dev.oblivion.client.module.world;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class VeinMiner extends Module {

    private final IntSetting maxBlocks = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Max Blocks")
            .description("Maximum blocks to mine in one vein.")
            .defaultValue(64)
            .range(1, 256)
            .build()
    );

    private final IntSetting range = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Range")
            .description("Search range for connected blocks.")
            .defaultValue(5)
            .range(1, 10)
            .build()
    );

    private final Set<BlockPos> toMine = new HashSet<>();
    private Block targetBlock = null;

    public VeinMiner() {
        super("VeinMiner", "Mines entire veins of ore or connected blocks.", Category.WORLD);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        // Start mining when attacking a block
        if (mc.options.attackKey.isPressed() && mc.crosshairTarget instanceof BlockHitResult blockHit) {
            if (blockHit.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = blockHit.getBlockPos();
                Block block = mc.world.getBlockState(pos).getBlock();

                if (targetBlock != block || toMine.isEmpty()) {
                    targetBlock = block;
                    toMine.clear();
                    findConnected(pos, block);
                }
            }
        }

        // Mine queued blocks
        if (!toMine.isEmpty()) {
            BlockPos next = toMine.iterator().next();
            toMine.remove(next);

            if (!mc.world.getBlockState(next).isAir()) {
                mc.player.networkHandler.sendPacket(
                    new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, next, Direction.UP)
                );
                mc.player.networkHandler.sendPacket(
                    new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, next, Direction.UP)
                );
            }
        }
    }

    private void findConnected(BlockPos start, Block block) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty() && toMine.size() < maxBlocks.get()) {
            BlockPos pos = queue.poll();
            toMine.add(pos);

            for (Direction dir : Direction.values()) {
                BlockPos neighbor = pos.offset(dir);
                if (visited.contains(neighbor)) continue;
                if (neighbor.getManhattanDistance(start) > range.get()) continue;
                visited.add(neighbor);

                BlockState state = mc.world.getBlockState(neighbor);
                if (state.getBlock() == block) {
                    queue.add(neighbor);
                }
            }
        }
    }

    @Override
    protected void onDisable() {
        toMine.clear();
        targetBlock = null;
    }
}
