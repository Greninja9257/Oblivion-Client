package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.util.math.BlockPos;

public final class WurstParkour extends Module {
    public WurstParkour() {
        super("Parkour", "Automatically jumps at block edges.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;
        if (!mc.player.isOnGround()) return;
        if (mc.player.input.movementForward <= 0) return;

        BlockPos ahead = BlockPos.ofFloored(
            mc.player.getX() + mc.player.getRotationVecClient().x * 0.45,
            mc.player.getY() - 0.1,
            mc.player.getZ() + mc.player.getRotationVecClient().z * 0.45
        );

        if (mc.world.getBlockState(ahead).isAir()) {
            mc.player.jump();
        }
    }
}
