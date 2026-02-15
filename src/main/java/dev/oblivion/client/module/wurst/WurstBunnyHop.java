package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;

public final class WurstBunnyHop extends Module {
    private final DoubleSetting speedBoost = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Speed Boost").description("Horizontal boost per hop").defaultValue(1.15).range(1.0, 2.5).build()
    );

    public WurstBunnyHop() {
        super("BunnyHop", "Auto-jumps and preserves strafe speed.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        if (mc.player.input.movementForward == 0 && mc.player.input.movementSideways == 0) return;

        if (mc.player.isOnGround()) {
            mc.player.jump();
            mc.player.setVelocity(
                mc.player.getVelocity().x * speedBoost.get(),
                mc.player.getVelocity().y,
                mc.player.getVelocity().z * speedBoost.get()
            );
        }
    }
}
