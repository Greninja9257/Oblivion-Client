package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.util.math.Vec3d;

public class HighJump extends Module {

    private final DoubleSetting multiplier = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Multiplier")
            .description("Jump height multiplier.")
            .defaultValue(2.0)
            .range(1.0, 10.0)
            .build()
    );

    private boolean wasOnGround = true;

    public HighJump() {
        super("HighJump", "Increases your jump height.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null) return;

        if (!mc.player.isOnGround() && wasOnGround && mc.player.getVelocity().y > 0) {
            Vec3d vel = mc.player.getVelocity();
            mc.player.setVelocity(vel.x, vel.y * multiplier.get(), vel.z);
        }
        wasOnGround = mc.player.isOnGround();
    }
}
