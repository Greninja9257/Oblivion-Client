package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.PacketEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

public final class WurstAntiKnockback extends Module {
    private final DoubleSetting horizontal = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Horizontal").description("Horizontal velocity percent").defaultValue(0.0).range(0.0, 100.0).build()
    );

    private final DoubleSetting vertical = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Vertical").description("Vertical velocity percent").defaultValue(0.0).range(0.0, 100.0).build()
    );

    private final BoolSetting explosions = settings.getDefaultGroup().add(
        new BoolSetting.Builder().name("Explosions").description("Modify explosion knockback too").defaultValue(true).build()
    );

    public WurstAntiKnockback() {
        super("AntiKnockback", "Reduces or removes knockback.", Category.COMBAT);
    }

    @EventHandler
    public void onPacket(PacketEvent.Receive event) {
        if (mc.player == null) return;

        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet) {
            if (packet.getEntityId() != mc.player.getId()) return;

            double h = horizontal.get() / 100.0;
            double v = vertical.get() / 100.0;
            event.cancel();
            mc.player.setVelocity(
                packet.getVelocityX() / 8000.0 * h,
                packet.getVelocityY() / 8000.0 * v,
                packet.getVelocityZ() / 8000.0 * h
            );
            return;
        }

        if (explosions.get() && event.getPacket() instanceof ExplosionS2CPacket) {
            if (horizontal.get() == 0.0 && vertical.get() == 0.0) {
                event.cancel();
            }
        }
    }
}
