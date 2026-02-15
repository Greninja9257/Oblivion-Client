package dev.oblivion.client.module.wurst;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;

public final class WurstPanic extends Module {
    public WurstPanic() {
        super("Panic", "Disables all active modules immediately.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        for (Module module : OblivionClient.get().moduleManager.getAll()) {
            if (module != this && module.isEnabled()) {
                module.disable();
            }
        }
        disable();
    }
}
