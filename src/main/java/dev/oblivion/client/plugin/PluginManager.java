package dev.oblivion.client.plugin;

import dev.oblivion.client.OblivionClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PluginManager {
    private final Path pluginsDir = Path.of("run", "oblivion-client", "plugins");

    public void init() {
        try {
            Files.createDirectories(pluginsDir);
        } catch (IOException e) {
            OblivionClient.LOGGER.error("Failed to create plugins directory", e);
        }
    }

    public void shutdown() {
    }
}
