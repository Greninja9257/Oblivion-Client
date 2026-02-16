package dev.oblivion.client.module.render;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;

public class Chams extends Module {

    private final BoolSetting players = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Players")
            .description("Apply chams to players.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting mobs = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Mobs")
            .description("Apply chams to mobs.")
            .defaultValue(false)
            .build()
    );

    private final BoolSetting throughWalls = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Through Walls")
            .description("See entities through walls.")
            .defaultValue(true)
            .build()
    );

    public Chams() {
        super("Chams", "Renders entities with a colored overlay, visible through walls.", Category.RENDER);
    }

    public boolean shouldRenderPlayers() { return players.get(); }
    public boolean shouldRenderMobs() { return mobs.get(); }
    public boolean shouldRenderThroughWalls() { return throughWalls.get(); }
}
