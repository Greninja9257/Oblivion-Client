package dev.oblivion.client.module.player;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.PacketEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AntiHunger extends Module {

    private final BoolSetting sprint = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Sprint")
            .description("Spoof not sprinting in movement packets to reduce server-side hunger drain.")
            .defaultValue(true)
            .build()
    );

    public AntiHunger() {
        super("AntiHunger", "Reduces hunger consumption by spoofing movement packets.", Category.PLAYER);
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send event) {
        if (mc.player == null) return;

        if (event.getPacket() instanceof ClientCommandC2SPacket packet && sprint.get()) {
            if (packet.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING) {
                event.cancel();
            }
        }
    }

    @EventHandler
    public void onPacketMove(PacketEvent.Send event) {
        if (mc.player == null) return;
        if (!(event.getPacket() instanceof PlayerMoveC2SPacket)) return;

        // Prevent client from staying in sprint state while this module is enabled.
        if (sprint.get() && mc.player.isSprinting()) {
            mc.player.setSprinting(false);
        }
    }
}
