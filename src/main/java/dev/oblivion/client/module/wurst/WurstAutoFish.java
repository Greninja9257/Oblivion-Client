package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.PacketEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

public final class WurstAutoFish extends Module {
    private final BoolSetting swing = settings.getDefaultGroup().add(
        new BoolSetting.Builder().name("Swing").description("Swing hand after reel/cast").defaultValue(true).build()
    );

    public WurstAutoFish() {
        super("AutoFish", "Automatically reels and recasts on fish bite sound.", Category.PLAYER);
    }

    @EventHandler
    public void onReceive(PacketEvent.Receive event) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (mc.player.getMainHandStack().getItem() != Items.FISHING_ROD) return;

        if (event.getPacket() instanceof PlaySoundS2CPacket sound) {
            if (sound.getSound().value() == SoundEvents.ENTITY_FISHING_BOBBER_SPLASH) {
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                if (swing.get()) mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }
}
