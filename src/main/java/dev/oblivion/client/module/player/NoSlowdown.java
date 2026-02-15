package dev.oblivion.client.module.player;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;

public class NoSlowdown extends Module {

    private final BoolSetting items = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Items")
            .description("Prevent slowdown while using items (eating, drinking, blocking).")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting webs = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Webs")
            .description("Prevent slowdown from cobwebs.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting soulSand = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Soul Sand")
            .description("Prevent slowdown from soul sand.")
            .defaultValue(true)
            .build()
    );

    public NoSlowdown() {
        super("NoSlowdown", "Prevents various sources of movement slowdown.", Category.PLAYER);
    }

    public boolean shouldCancelItemSlow() {
        return isEnabled() && items.get();
    }

    public boolean shouldCancelWebSlow() {
        return isEnabled() && webs.get();
    }

    public boolean shouldCancelSoulSandSlow() {
        return isEnabled() && soulSand.get();
    }
}
