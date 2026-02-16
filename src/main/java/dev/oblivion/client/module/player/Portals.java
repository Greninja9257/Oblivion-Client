package dev.oblivion.client.module.player;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;

public class Portals extends Module {

    private final BoolSetting chat = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Chat")
            .description("Allow opening chat in portals.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting inventory = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Inventory")
            .description("Allow opening inventory in portals.")
            .defaultValue(true)
            .build()
    );

    public Portals() {
        super("Portals", "Allows you to use GUIs in nether portals.", Category.PLAYER);
    }

    public boolean allowChat() { return chat.get(); }
    public boolean allowInventory() { return inventory.get(); }
}
