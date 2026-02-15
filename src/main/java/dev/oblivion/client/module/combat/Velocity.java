package dev.oblivion.client.module.combat;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.PacketEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

public class Velocity extends Module {

    private final DoubleSetting horizontal = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Horizontal")
            .description("Horizontal velocity multiplier (0 = no knockback).")
            .defaultValue(0.0)
            .range(0.0, 100.0)
            .build()
    );

    private final DoubleSetting vertical = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Vertical")
            .description("Vertical velocity multiplier (0 = no knockback).")
            .defaultValue(0.0)
            .range(0.0, 100.0)
            .build()
    );

    public Velocity() {
        super("Velocity", "Modifies incoming knockback velocity.", Category.COMBAT);
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (mc.player == null || mc.world == null) return;

        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet) {
            if (packet.getEntityId() != mc.player.getId()) return;

            double hFactor = horizontal.get() / 100.0;
            double vFactor = vertical.get() / 100.0;

            if (hFactor == 0 && vFactor == 0) {
                // Cancel the packet entirely for zero velocity
                event.cancel();
                return;
            }

            // Apply modified velocity after the packet is processed
            event.cancel();
            mc.player.setVelocity(
                packet.getVelocityX() / 8000.0 * hFactor,
                packet.getVelocityY() / 8000.0 * vFactor,
                packet.getVelocityZ() / 8000.0 * hFactor
            );
        }

        if (event.getPacket() instanceof ExplosionS2CPacket packet) {
            double hFactor = horizontal.get() / 100.0;
            double vFactor = vertical.get() / 100.0;

            if (hFactor == 0 && vFactor == 0) {
                event.cancel();
                return;
            }

            // Let the packet through but modify player velocity afterward
            // Explosion velocity will be handled on next tick
        }
    }
}
