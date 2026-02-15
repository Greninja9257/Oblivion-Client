package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;

public final class WurstForceOp extends Module {
    private final IntSetting interval = settings.getDefaultGroup().add(
        new IntSetting.Builder().name("Interval").description("Ticks between /op attempts").defaultValue(40).range(5, 200).build()
    );

    private int ticks;

    public WurstForceOp() {
        super("ForceOp", "Continuously sends /op for your own username.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        ticks = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.player.networkHandler == null) return;

        ticks++;
        if (ticks < interval.get()) return;
        ticks = 0;

        mc.player.networkHandler.sendChatCommand("op " + mc.player.getName().getString());
    }
}
