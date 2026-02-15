package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;

public class Spider extends Module {

    private final DoubleSetting speed = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Speed")
            .description("Wall climb speed.")
            .defaultValue(0.2)
            .min(0.1)
            .max(1.0)
            .build()
    );

    public Spider() {
        super("Spider", "Climb walls like a spider.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.horizontalCollision && !mc.player.isOnGround()) {
            mc.player.setVelocity(
                mc.player.getVelocity().x,
                speed.get(),
                mc.player.getVelocity().z
            );
        }
    }
}
