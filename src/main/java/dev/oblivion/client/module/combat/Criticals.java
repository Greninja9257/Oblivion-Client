package dev.oblivion.client.module.combat;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.PacketEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.EnumSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Criticals extends Module {

    public enum Mode { PACKET, JUMP }

    private final EnumSetting<Mode> mode = settings.getDefaultGroup().add(
        new EnumSetting.Builder<Mode>()
            .name("Mode")
            .description("Method for triggering critical hits.")
            .defaultValue(Mode.PACKET)
            .build()
    );

    public Criticals() {
        super("Criticals", "Makes all hits critical hits.", Category.COMBAT);
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send event) {
        if (mc.player == null || mc.world == null) return;
        if (!(event.getPacket() instanceof PlayerInteractEntityC2SPacket)) return;

        // Only apply when player is on ground and not in water/lava
        if (!mc.player.isOnGround()) return;
        if (mc.player.isSubmergedInWater() || mc.player.isInLava()) return;
        if (mc.player.isClimbing() || mc.player.hasVehicle()) return;

        if (mode.get() == Mode.PACKET) {
            // Send small position offset packets to simulate a fall
            double x = mc.player.getX();
            double y = mc.player.getY();
            double z = mc.player.getZ();

            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                x, y + 0.0625, z, false, mc.player.horizontalCollision
            ));
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                x, y, z, false, mc.player.horizontalCollision
            ));
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                x, y + 1.1e-5, z, false, mc.player.horizontalCollision
            ));
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                x, y, z, true, mc.player.horizontalCollision
            ));
        } else {
            // Jump mode - perform a small jump
            mc.player.jump();
        }
    }
}
