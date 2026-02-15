package dev.oblivion.client.module.wurst;

import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.client.option.Perspective;

public final class WurstCameraDistance extends Module {
    private Perspective previous;

    public WurstCameraDistance() {
        super("CameraDistance", "Switches to third-person view.", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        previous = mc.options.getPerspective();
        mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
    }

    @Override
    protected void onDisable() {
        if (previous != null) {
            mc.options.setPerspective(previous);
        }
    }
}
