package dev.oblivion.client.module.player;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.PacketEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class NoRotate extends Module {

    public NoRotate() {
        super("NoRotate", "Prevents the server from rotating your view.", Category.PLAYER);
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (mc.player == null) return;
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            // Cancel the rotation portion of the teleport packet
            // The actual position will still be applied, but not the rotation
            // Note: Full implementation requires mixin to modify the packet fields
        }
    }
}
