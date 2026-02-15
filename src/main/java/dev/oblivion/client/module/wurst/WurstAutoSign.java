package dev.oblivion.client.module.wurst;

import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;

public final class WurstAutoSign extends Module {
    public WurstAutoSign() {
        super("AutoSign", "Automatically closes sign edit screens.", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.currentScreen instanceof AbstractSignEditScreen) {
            mc.setScreen(null);
        }
    }
}
