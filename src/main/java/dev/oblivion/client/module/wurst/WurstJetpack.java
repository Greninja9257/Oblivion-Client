package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;

public final class WurstJetpack extends Module {
    private final DoubleSetting thrust = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Thrust").description("Upward thrust per tick").defaultValue(0.12).range(0.02, 0.6).build()
    );

    public WurstJetpack() {
        super("Jetpack", "Provides upward thrust while jump is held.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        if (!mc.options.jumpKey.isPressed()) return;

        mc.player.setVelocity(mc.player.getVelocity().x, thrust.get(), mc.player.getVelocity().z);
    }
}
