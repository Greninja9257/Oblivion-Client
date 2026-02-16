package dev.oblivion.client.module.render;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;

public class ItemHighlight extends Module {

    private final IntSetting range = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Range")
            .description("Range to highlight dropped items.")
            .defaultValue(32)
            .range(8, 128)
            .build()
    );

    public ItemHighlight() {
        super("ItemHighlight", "Highlights dropped items in the world.", Category.RENDER);
    }

    public int getRange() { return range.get(); }
}
