package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import net.minecraft.util.math.BlockPos;

public class SafeWalk extends Module {

    private final BoolSetting onlyOnEdge = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Only On Edge")
            .description("Only activate when at the edge of a block.")
            .defaultValue(true)
            .build()
    );

    public SafeWalk() {
        super("SafeWalk", "Prevents you from falling off edges.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;
        if (!mc.player.isOnGround()) return;
        if (mc.player.isSneaking()) return;

        mc.player.setSneaking(true);
    }

    @Override
    protected void onDisable() {
        if (mc.player != null && !mc.options.sneakKey.isPressed()) {
            mc.player.setSneaking(false);
        }
    }
}
