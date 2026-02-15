package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;

public final class WurstGlide extends Module {
    private final DoubleSetting descent = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Descent").description("Downward speed cap").defaultValue(-0.06).range(-0.2, 0.0).build()
    );

    public WurstGlide() {
        super("Glide", "Slows falling for a glide effect.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        if (mc.player.isOnGround()) return;

        if (mc.player.getVelocity().y < descent.get()) {
            mc.player.setVelocity(mc.player.getVelocity().x, descent.get(), mc.player.getVelocity().z);
        }
    }
}
