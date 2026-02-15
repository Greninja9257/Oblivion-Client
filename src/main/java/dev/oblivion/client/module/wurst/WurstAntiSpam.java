package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;

public final class WurstAntiSpam extends Module {
    private final IntSetting clearInterval = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Clear Interval").description("Ticks between chat clears").defaultValue(200).range(20, 1200).build()
    );

    private int ticks;

    public WurstAntiSpam() {
        super("AntiSpam", "Periodically clears chat to suppress spam buildup.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        ticks = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.inGameHud == null) return;
        if (++ticks >= clearInterval.get()) {
            ticks = 0;
            mc.inGameHud.getChatHud().clear(false);
        }
    }
}
