package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;

public final class WurstFastLadder extends Module {
    private final DoubleSetting climbSpeed = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Climb Speed").description("Vertical climb speed").defaultValue(0.35).range(0.1, 1.0).build()
    );

    public WurstFastLadder() {
        super("FastLadder", "Climbs ladders/vines faster.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        if (!mc.player.isClimbing()) return;

        double y = climbSpeed.get();
        if (mc.options.sneakKey.isPressed()) y = -y;
        mc.player.setVelocity(mc.player.getVelocity().x, y, mc.player.getVelocity().z);
    }
}
