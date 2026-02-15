package dev.oblivion.client.module.wurst;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstCameraNoClip extends Module {
    public WurstCameraNoClip() {
        super("CameraNoClip", "Enables free camera clipping through blocks.", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        var freecam = dev.oblivion.client.OblivionClient.get().moduleManager.get(dev.oblivion.client.module.render.Freecam.class);
        if (freecam != null && !freecam.isEnabled()) {
            freecam.enable();
        }
    }

    @Override
    protected void onDisable() {
        var freecam = dev.oblivion.client.OblivionClient.get().moduleManager.get(dev.oblivion.client.module.render.Freecam.class);
        if (freecam != null && freecam.isEnabled()) {
            freecam.disable();
        }
    }
}
