package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public class AirJump extends Module {

    private boolean wasPressed = false;

    public AirJump() {
        super("AirJump", "Allows you to jump in the air.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        boolean pressed = mc.options.jumpKey.isPressed();
        if (pressed && !wasPressed && !mc.player.isOnGround()) {
            mc.player.jump();
        }
        wasPressed = pressed;
    }
}
