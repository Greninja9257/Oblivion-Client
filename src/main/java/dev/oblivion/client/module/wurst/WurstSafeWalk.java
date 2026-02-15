package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import net.minecraft.util.math.BlockPos;

public final class WurstSafeWalk extends Module {
    private final BoolSetting onlyWhenGrounded = settings.getDefaultGroup().add(
        new BoolSetting.Builder().name("Only Grounded").description("Apply only while on ground").defaultValue(true).build()
    );

    public WurstSafeWalk() {
        super("SafeWalk", "Sneaks automatically near block edges.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;
        if (onlyWhenGrounded.get() && !mc.player.isOnGround()) return;

        BlockPos below = BlockPos.ofFloored(mc.player.getX(), mc.player.getY() - 0.2, mc.player.getZ());
        boolean overAir = mc.world.getBlockState(below).isAir();

        if (overAir) {
            mc.options.sneakKey.setPressed(true);
        }
    }

    @Override
    protected void onDisable() {
        if (mc.options != null) {
            mc.options.sneakKey.setPressed(false);
        }
    }
}
