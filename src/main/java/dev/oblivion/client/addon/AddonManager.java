package dev.oblivion.client.addon;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.addon.marketplace.MarketplaceClient;
import dev.oblivion.client.addon.marketplace.MarketplaceEntry;
import dev.oblivion.client.addon.marketplace.VoteStore;
import dev.oblivion.client.module.Module;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central manager for the addon system.
 * Handles loading installed addons, installing/uninstalling from marketplace, and vote persistence.
 */
public class AddonManager {
    private final Path addonsDir = Path.of("run", "oblivion-client", "addons");
    private final Path cacheDir = Path.of("run", "oblivion-client", "marketplace-cache");

    private final AddonLoader loader = new AddonLoader();
    private final MarketplaceClient marketplaceClient;
    private final VoteStore voteStore;
    private final Map<String, Addon> installedAddons = new ConcurrentHashMap<>();

    public AddonManager() {
        this.marketplaceClient = new MarketplaceClient(cacheDir);
        this.voteStore = new VoteStore(cacheDir.resolve("votes.json"));
    }

    public void init() {
        try {
            Files.createDirectories(addonsDir);
            Files.createDirectories(cacheDir);
        } catch (IOException e) {
            OblivionClient.LOGGER.error("Failed to create addon directories", e);
        }

        voteStore.load();
        loadInstalledAddons();
    }

    private void loadInstalledAddons() {
        if (!Files.isDirectory(addonsDir)) return;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(addonsDir)) {
            for (Path dir : stream) {
                if (!Files.isDirectory(dir)) continue;
                if (!Files.exists(dir.resolve("addon.json"))) continue;

                try {
                    Addon addon = loader.loadAddon(dir);
                    installedAddons.put(addon.getId(), addon);

                    Module module = addon.getLoadedModule();
                    if (module != null) {
                        OblivionClient.get().moduleManager.register(module);
                    }

                    OblivionClient.LOGGER.info("Loaded addon: {} v{}", addon.getName(), addon.getVersion());
                } catch (AddonException e) {
                    OblivionClient.LOGGER.error("Failed to load addon from {}: {}", dir.getFileName(), e.getMessage());
                }
            }
        } catch (IOException e) {
            OblivionClient.LOGGER.error("Failed to scan addons directory", e);
        }
    }

    /**
     * Installs an addon from the marketplace asynchronously.
     */
    public CompletableFuture<Void> installAddon(MarketplaceEntry entry) {
        Path targetDir = addonsDir.resolve(entry.getId());

        if (Files.exists(targetDir)) {
            deleteDirectory(targetDir);
        }

        return marketplaceClient.downloadAddon(entry, targetDir).thenRun(() -> {
            try {
                Addon addon = loader.loadAddon(targetDir);
                installedAddons.put(addon.getId(), addon);

                Module module = addon.getLoadedModule();
                if (module != null) {
                    OblivionClient.get().moduleManager.register(module);
                }

                OblivionClient.LOGGER.info("Installed addon: {}", addon.getName());
            } catch (AddonException e) {
                OblivionClient.LOGGER.error("Failed to load installed addon: {}", e.getMessage());
                deleteDirectory(targetDir);
                throw new RuntimeException("Failed to load addon '" + entry.getName() + "': " + e.getMessage(), e);
            }
        });
    }

    /**
     * Uninstalls an addon by ID.
     */
    public void uninstallAddon(String addonId) {
        Addon addon = installedAddons.remove(addonId);
        if (addon == null) return;

        Module module = addon.getLoadedModule();
        if (module != null) {
            if (module.isEnabled()) module.disable();
            OblivionClient.get().moduleManager.unregister(module);
        }

        loader.unloadAddon(addonId);
        deleteDirectory(addonsDir.resolve(addonId));

        OblivionClient.LOGGER.info("Uninstalled addon: {}", addon.getName());
    }

    public boolean isInstalled(String addonId) {
        return installedAddons.containsKey(addonId);
    }

    public MarketplaceClient getMarketplaceClient() { return marketplaceClient; }
    public VoteStore getVoteStore() { return voteStore; }
    public Map<String, Addon> getInstalledAddons() { return Collections.unmodifiableMap(Map.copyOf(installedAddons)); }

    public void shutdown() {
        for (String id : installedAddons.keySet()) {
            loader.unloadAddon(id);
        }
    }

    private void deleteDirectory(Path dir) {
        if (!Files.exists(dir)) return;
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path d, IOException exc) throws IOException {
                    Files.delete(d);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            OblivionClient.LOGGER.warn("Failed to delete directory: {}", dir, e);
        }
    }
}
