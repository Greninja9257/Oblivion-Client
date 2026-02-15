package dev.oblivion.client.module.wurst;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstNoHurtcam extends Module {
    public WurstNoHurtcam() {
        super("NoHurtcam", "Disables hurt camera shake.", Category.RENDER);
    }
}
