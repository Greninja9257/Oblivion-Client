package dev.oblivion.client.module.wurst;

import dev.oblivion.client.gui.screen.ClickGuiScreen;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstClickGui extends Module {
    public WurstClickGui() {
        super("ClickGui", "Opens the ClickGUI screen.", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        if (mc.currentScreen == null) {
            mc.setScreen(new ClickGuiScreen());
        }
        disable();
    }
}
