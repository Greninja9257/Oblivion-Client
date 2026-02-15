package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.EnumSetting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Blocks;

public class Jesus extends Module {

    public enum Mode { SOLID, DOLPHIN }

    private final EnumSetting<Mode> mode = settings.getDefaultGroup().add(
        new EnumSetting.Builder<Mode>()
            .name("Mode")
            .description("Water walking mode.")
            .defaultValue(Mode.SOLID)
            .build()
    );

    public Jesus() {
        super("Jesus", "Allows you to walk on water.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        switch (mode.get()) {
            case SOLID -> {
                // Solid mode is primarily handled by MixinFluidBlock making
                // water/lava collidable. Here we handle the edge case of
                // the player being slightly submerged.
                if (mc.player.isTouchingWater() && !mc.player.isSneaking()) {
                    mc.player.setVelocity(
                        mc.player.getVelocity().x,
                        0.11,
                        mc.player.getVelocity().z
                    );
                }
            }
            case DOLPHIN -> {
                if (mc.player.isTouchingWater()) {
                    mc.player.setVelocity(
                        mc.player.getVelocity().x,
                        0.45,
                        mc.player.getVelocity().z
                    );
                    mc.player.setSprinting(true);
                }
            }
        }
    }

    /**
     * Returns true if SOLID mode is active. Used by MixinFluidBlock to
     * determine whether fluid blocks should have solid collision.
     */
    public boolean isSolidMode() {
        return isEnabled() && mode.get() == Mode.SOLID;
    }
}
