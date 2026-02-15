package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;
import net.minecraft.entity.player.PlayerEntity;

public final class WurstMassTpa extends Module {
    private final IntSetting interval = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Interval").description("Ticks between /tpa waves").defaultValue(100).range(20, 600).build()
    );

    private int ticks;

    public WurstMassTpa() {
        super("MassTpa", "Sends /tpa requests to all visible players.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        ticks = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null || mc.player.networkHandler == null) return;

        ticks++;
        if (ticks < interval.get()) return;
        ticks = 0;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            mc.player.networkHandler.sendChatCommand("tpa " + player.getName().getString());
        }
    }
}
