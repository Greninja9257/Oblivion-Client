package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstAntiAfk extends Module {
    private int ticks;

    public WurstAntiAfk() {
        super("AntiAfk", "Performs light movement to prevent AFK kicks.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        ticks = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        ticks++;

        if (ticks % 40 == 0) {
            mc.player.setYaw(mc.player.getYaw() + 20f);
        }

        if (ticks % 80 == 0 && mc.player.isOnGround()) {
            mc.player.jump();
        }
    }
}
