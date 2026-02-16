package dev.oblivion.client.module.render;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.IntSetting;

public class LightOverlay extends Module {

    private final IntSetting range = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Range")
            .description("Range to show light level overlay.")
            .defaultValue(8)
            .range(1, 32)
            .build()
    );

    private final IntSetting threshold = settings.getDefaultGroup().add(
        new IntSetting.Builder()
            .name("Threshold")
            .description("Light level at or below which mobs can spawn (shown in red).")
            .defaultValue(0)
            .range(0, 15)
            .build()
    );

    public LightOverlay() {
        super("LightOverlay", "Shows light level overlay on blocks to identify mob spawn areas.", Category.RENDER);
    }

    public int getRange() { return range.get(); }
    public int getThreshold() { return threshold.get(); }
}
