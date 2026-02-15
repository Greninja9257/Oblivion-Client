package dev.oblivion.client;

import net.fabricmc.api.ClientModInitializer;

public class OblivionMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OblivionClient.get().initialize();
    }
}
