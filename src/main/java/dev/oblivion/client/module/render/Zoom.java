package dev.oblivion.client.module.render;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.DoubleSetting;
import dev.oblivion.client.setting.impl.BoolSetting;

public class Zoom extends Module {

    private final DoubleSetting factor = settings.getDefaultGroup().add(
        new DoubleSetting.Builder()
            .name("Factor")
            .description("Zoom factor.")
            .defaultValue(4.0)
            .range(1.0, 30.0)
            .build()
    );

    private final BoolSetting smooth = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Smooth")
            .description("Smooth camera when zoomed.")
            .defaultValue(true)
            .build()
    );

    private final BoolSetting scroll = settings.getDefaultGroup().add(
        new BoolSetting.Builder()
            .name("Scroll")
            .description("Allow adjusting zoom with scroll wheel.")
            .defaultValue(true)
            .build()
    );

    private double currentFactor;

    public Zoom() {
        super("Zoom", "Zooms in your view.", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        currentFactor = factor.get();
    }

    public double getFactor() { return currentFactor; }
    public boolean isSmooth() { return smooth.get(); }

    public void adjustFactor(double delta) {
        if (scroll.get()) {
            currentFactor = Math.max(1.0, Math.min(30.0, currentFactor + delta));
        }
    }
}
