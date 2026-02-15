package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.PacketEvent;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.mixin.PlayerMoveC2SPacketAccessor;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.EnumSetting;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoFall extends Module {

    public enum Mode { PACKET, ON_GROUND }

    private final EnumSetting<Mode> mode = settings.getDefaultGroup().add(
        new EnumSetting.Builder<Mode>()
            .name("Mode")
            .description("NoFall mode.")
            .defaultValue(Mode.PACKET)
            .build()
    );

    public NoFall() {
        super("NoFall", "Prevents fall damage.", Category.MOVEMENT);
    }

    @EventHandler
    public void onSendPacket(PacketEvent.Send event) {
        if (mc.player == null) return;
        if (mode.get() != Mode.PACKET) return;

        if (event.getPacket() instanceof PlayerMoveC2SPacket packet) {
            if (mc.player.fallDistance > 2.0f) {
                ((PlayerMoveC2SPacketAccessor) packet).setOnGround(true);
            }
        }
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;
        if (mode.get() != Mode.ON_GROUND) return;

        if (mc.player.fallDistance > 2.0f) {
            mc.player.setOnGround(true);
        }
    }
}
