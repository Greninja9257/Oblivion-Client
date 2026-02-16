package dev.oblivion.client.module.player;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PacketMine extends Module {

    private final DoubleSetting range = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Range")
            .description("Mining range.")
            .defaultValue(4.5)
            .range(1.0, 6.0)
            .build()
    );

    private BlockPos miningPos = null;
    private Direction miningDir = Direction.UP;

    public PacketMine() {
        super("PacketMine", "Mines blocks using packets, allowing you to walk away while mining.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        // If we have a block we're mining, keep sending the finish packet
        if (miningPos != null) {
            if (mc.player.getBlockPos().getSquaredDistance(miningPos) > range.get() * range.get()) {
                miningPos = null;
                return;
            }
            if (mc.world.getBlockState(miningPos).isAir()) {
                miningPos = null;
                return;
            }

            mc.player.networkHandler.sendPacket(
                new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, miningPos, miningDir)
            );
            return;
        }

        // Start mining what we're looking at
        if (mc.options.attackKey.isPressed() && mc.crosshairTarget instanceof BlockHitResult blockHit) {
            if (blockHit.getType() == HitResult.Type.BLOCK) {
                miningPos = blockHit.getBlockPos();
                miningDir = blockHit.getSide();

                mc.player.networkHandler.sendPacket(
                    new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, miningPos, miningDir)
                );
                mc.player.networkHandler.sendPacket(
                    new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, miningPos, miningDir)
                );
            }
        }
    }

    @Override
    protected void onDisable() {
        miningPos = null;
    }
}
