package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstTooManyHax extends Module {
    private int ticks;

    public WurstTooManyHax() {
        super("TooManyHax", "Chaotic stress module that randomizes local motion/camera.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        ticks = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        ticks++;
        if (ticks % 4 == 0) {
            mc.player.setYaw(mc.player.getYaw() + (float) ((Math.random() - 0.5) * 50));
            mc.player.setPitch((float) Math.max(-89, Math.min(89, mc.player.getPitch() + (Math.random() - 0.5) * 30)));
        }

        if (ticks % 8 == 0) {
            double vx = (Math.random() - 0.5) * 0.6;
            double vz = (Math.random() - 0.5) * 0.6;
            mc.player.setVelocity(vx, mc.player.getVelocity().y, vz);
        }
    }
}
