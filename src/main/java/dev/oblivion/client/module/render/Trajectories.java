package dev.oblivion.client.module.render;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import net.minecraft.item.*;

public class Trajectories extends Module {

    private final BoolSetting bows = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Bows")
            .description("Show bow trajectories.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting pearls = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Ender Pearls")
            .description("Show ender pearl trajectories.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting potions = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Potions")
            .description("Show splash potion trajectories.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting snowballs = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Snowballs/Eggs")
            .description("Show snowball and egg trajectories.")
            .defaultValue(true)
            .build()
    );

    public Trajectories() {
        super("Trajectories", "Shows the predicted path of thrown projectiles.", Category.RENDER);
    }

    public boolean shouldShow(Item item) {
        if (item instanceof BowItem && bows.get()) return true;
        if (item instanceof CrossbowItem && bows.get()) return true;
        if (item instanceof EnderPearlItem && pearls.get()) return true;
        if (item instanceof SplashPotionItem && potions.get()) return true;
        if (item instanceof LingeringPotionItem && potions.get()) return true;
        if (item instanceof SnowballItem && snowballs.get()) return true;
        if (item instanceof EggItem && snowballs.get()) return true;
        return false;
    }
}
