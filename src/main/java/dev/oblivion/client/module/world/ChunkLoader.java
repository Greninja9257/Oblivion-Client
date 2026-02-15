package dev.oblivion.client.module.world;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class ChunkLoader extends Module {

    private int timer = 0;

    public ChunkLoader() {
        super("ChunkLoader", "Keeps chunks loaded by sending periodic position packets.", Category.WORLD);
    }

    @Override
    protected void onEnable() {
        timer = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        timer++;

        // Send a position packet every 20 ticks to keep chunks loaded
        if (timer >= 20) {
            timer = 0;

            double x = mc.player.getX();
            double y = mc.player.getY();
            double z = mc.player.getZ();
            boolean onGround = mc.player.isOnGround();

            mc.player.networkHandler.sendPacket(
                new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround, mc.player.horizontalCollision)
            );
        }
    }
}
