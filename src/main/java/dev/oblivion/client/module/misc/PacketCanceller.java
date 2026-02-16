package dev.oblivion.client.module.misc;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.PacketEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;

public class PacketCanceller extends Module {

    private final BoolSetting particles = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Particles")
            .description("Cancel particle packets.")
            .defaultValue(false)
            .build()
    );

    private final BoolSetting sounds = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Sounds")
            .description("Cancel sound packets.")
            .defaultValue(false)
            .build()
    );

    private final BoolSetting explosions = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Explosions")
            .description("Cancel explosion packets (visual only).")
            .defaultValue(false)
            .build()
    );

    private final BoolSetting worldEvents = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("World Events")
            .description("Cancel world event packets.")
            .defaultValue(false)
            .build()
    );

    public PacketCanceller() {
        super("PacketCanceller", "Cancels specific incoming packets.", Category.MISC);
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (particles.get() && event.getPacket() instanceof ParticleS2CPacket) event.cancel();
        if (sounds.get() && event.getPacket() instanceof PlaySoundS2CPacket) event.cancel();
        if (explosions.get() && event.getPacket() instanceof ExplosionS2CPacket) event.cancel();
        if (worldEvents.get() && event.getPacket() instanceof WorldEventS2CPacket) event.cancel();
    }
}
