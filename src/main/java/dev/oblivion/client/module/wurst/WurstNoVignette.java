package dev.oblivion.client.module.wurst;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstNoVignette extends Module {
    private double oldGamma;

    public WurstNoVignette() {
        super("NoVignette", "Boosts gamma to eliminate vignette-like darkness.", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        oldGamma = mc.options.getGamma().getValue();
        mc.options.getGamma().setValue(Math.max(oldGamma, 8.0));
    }

    @Override
    protected void onDisable() {
        mc.options.getGamma().setValue(oldGamma);
    }
}
