package dev.oblivion.client.module.player;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;

public class PotionSaver extends Module {

    private final BoolSetting onlyWhenStanding = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Only When Standing")
            .description("Only pause potions when standing still.")
            .defaultValue(true)
            .build()
    );

    public PotionSaver() {
        super("PotionSaver", "Pauses potion effects when standing still to make them last longer.", Category.PLAYER);
    }

    public boolean shouldPause() {
        if (mc.player == null) return false;
        if (onlyWhenStanding.get()) {
            return mc.player.input.movementForward == 0
                && mc.player.input.movementSideways == 0
                && !mc.options.jumpKey.isPressed();
        }
        return true;
    }
}
