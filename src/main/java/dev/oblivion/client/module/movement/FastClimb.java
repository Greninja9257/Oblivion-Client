package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import net.minecraft.util.math.Vec3d;

public class FastClimb extends Module {

    private final DoubleSetting speed = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Speed")
            .description("Climbing speed multiplier.")
            .defaultValue(1.5)
            .range(0.5, 5.0)
            .build()
    );

    public FastClimb() {
        super("FastClimb", "Climb ladders and vines faster.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null) return;
        if (!mc.player.isClimbing()) return;

        Vec3d velocity = mc.player.getVelocity();
        if (mc.options.forwardKey.isPressed() || mc.options.jumpKey.isPressed()) {
            mc.player.setVelocity(velocity.x, 0.2 * speed.get(), velocity.z);
        }
    }
}
