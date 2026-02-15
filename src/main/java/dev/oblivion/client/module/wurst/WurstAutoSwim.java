package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;

public final class WurstAutoSwim extends Module {
    private final DoubleSetting upSpeed = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Up Speed").description("Upward swim speed").defaultValue(0.12).range(0.03, 0.4).build()
    );

    public WurstAutoSwim() {
        super("AutoSwim", "Automatically rises while in water.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        if (!mc.player.isTouchingWater()) return;

        mc.player.setVelocity(mc.player.getVelocity().x, upSpeed.get(), mc.player.getVelocity().z);
    }
}
