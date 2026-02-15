package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;

public final class WurstAutoSprint extends Module {
    private final BoolSetting omni = settings.getDefaultGroup().add(
        new BoolSetting.Builder().name("Omni").description("Sprint while strafing/backwards too").defaultValue(false).build()
    );

    public WurstAutoSprint() {
        super("AutoSprint", "Automatically sprints while moving.", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;
        if (mc.player.isSneaking()) return;
        if (mc.player.getHungerManager().getFoodLevel() <= 6) return;

        boolean movingForward = mc.player.input.movementForward > 0;
        boolean movingAny = movingForward || mc.player.input.movementSideways != 0 || mc.player.input.movementForward < 0;

        if ((omni.get() && movingAny) || (!omni.get() && movingForward)) {
            mc.player.setSprinting(true);
        }
    }
}
