package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;

public final class WurstHighJump extends Module {
    private final DoubleSetting jumpVelocity = settings.getDefaultGroup().add(
        new DoubleSetting.Builder().name("Jump Velocity").description("Vertical jump velocity").defaultValue(0.65).range(0.42, 1.8).build()
    );

    public WurstHighJump() {
        super("HighJump", "Increases jump height.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        if (!mc.options.jumpKey.isPressed()) return;
        if (!mc.player.isOnGround()) return;

        mc.player.setVelocity(mc.player.getVelocity().x, jumpVelocity.get(), mc.player.getVelocity().z);
    }
}
