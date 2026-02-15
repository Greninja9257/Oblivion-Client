package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstNoBackground extends Module {
    private double oldGamma;

    public WurstNoBackground() {
        super("NoBackground", "Removes perceived GUI darkening by boosting gamma while screens are open.", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        oldGamma = mc.options.getGamma().getValue();
    }

    @Override
    protected void onDisable() {
        mc.options.getGamma().setValue(oldGamma);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.currentScreen != null) {
            mc.options.getGamma().setValue(Math.max(oldGamma, 12.0));
        } else {
            mc.options.getGamma().setValue(oldGamma);
        }
    }
}
