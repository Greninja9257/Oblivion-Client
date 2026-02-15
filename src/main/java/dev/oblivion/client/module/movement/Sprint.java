package dev.oblivion.client.module.movement;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;

public class Sprint extends Module {

    private final BoolSetting rage = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Rage")
            .description("Sprint in all directions, not just forward.")
            .defaultValue(false)
            .build()
    );

    public Sprint() {
        super("Sprint", "Automatically sprints for you.", Category.MOVEMENT);
    }

    @Override
    protected void onDisable() {
        if (mc.player != null) {
            mc.player.setSprinting(false);
        }
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;
        if (mc.player.getHungerManager().getFoodLevel() <= 6) return;

        if (rage.get()) {
            if (mc.player.input.movementForward != 0 || mc.player.input.movementSideways != 0) {
                mc.player.setSprinting(true);
            }
        } else {
            if (mc.player.input.movementForward > 0) {
                mc.player.setSprinting(true);
            }
        }
    }
}
