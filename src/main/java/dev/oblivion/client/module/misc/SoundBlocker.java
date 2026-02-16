package dev.oblivion.client.module.misc;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.PacketEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundCategory;

public class SoundBlocker extends Module {

    private final BoolSetting weather = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Weather")
            .description("Block weather sounds.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting mobs = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Mobs")
            .description("Block mob sounds.")
            .defaultValue(false)
            .build()
    );

    private final BoolSetting ambient = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Ambient")
            .description("Block ambient sounds.")
            .defaultValue(false)
            .build()
    );

    private final BoolSetting music = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Music")
            .description("Block background music.")
            .defaultValue(false)
            .build()
    );

    public SoundBlocker() {
        super("SoundBlocker", "Blocks specific categories of sounds.", Category.MISC);
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!(event.getPacket() instanceof PlaySoundS2CPacket packet)) return;

        SoundCategory category = packet.getCategory();
        if (weather.get() && category == SoundCategory.WEATHER) event.cancel();
        if (mobs.get() && (category == SoundCategory.HOSTILE || category == SoundCategory.NEUTRAL)) event.cancel();
        if (ambient.get() && category == SoundCategory.AMBIENT) event.cancel();
        if (music.get() && category == SoundCategory.MUSIC) event.cancel();
    }
}
