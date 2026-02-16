package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.PacketEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.ArrayList;
import java.util.List;

public class Blink extends Module {

    private final List<Packet<?>> packets = new ArrayList<>();

    public Blink() {
        super("Blink", "Holds movement packets and sends them all at once when disabled.", Category.MOVEMENT);
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send event) {
        if (mc.player == null) return;
        if (event.getPacket() instanceof PlayerMoveC2SPacket) {
            packets.add(event.getPacket());
            event.cancel();
        }
    }

    @Override
    protected void onDisable() {
        if (mc.player != null && mc.player.networkHandler != null) {
            for (Packet<?> packet : packets) {
                mc.player.networkHandler.sendPacket(packet);
            }
        }
        packets.clear();
    }

    @Override
    protected void onEnable() {
        packets.clear();
    }
}
