package dev.oblivion.client.addon.marketplace;

import dev.oblivion.client.OblivionClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP client for the GitHub-backed addon marketplace.
 * Fetches the addon index, downloads addon files, and caches results locally.
 */
public class MarketplaceClient {
    private static final String DEFAULT_REPO_URL =
        "https://raw.githubusercontent.com/Greninja9257/Oblivion-Client/main/marketplace";
    private static final long CACHE_DURATION_MS = 5 * 60 * 1000; // 5 minutes
    private static final int TIMEOUT_MS = 10000;

    private final Path cacheDir;
    private String repoUrl = DEFAULT_REPO_URL;
    private volatile MarketplaceIndex cachedIndex;
    private volatile long lastFetchTime = 0;

    public MarketplaceClient(Path cacheDir) {
        this.cacheDir = cacheDir;
    }

    public void setRepoUrl(String url) {
        this.repoUrl = url;
        this.cachedIndex = null;
        this.lastFetchTime = 0;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    /**
     * Fetches the marketplace index asynchronously.
     * Uses in-memory cache if fresh, falls back to disk cache on network failure.
     */
    public CompletableFuture<MarketplaceIndex> fetchIndex() {
        return CompletableFuture.supplyAsync(() -> {
            long now = System.currentTimeMillis();
            if (cachedIndex != null && (now - lastFetchTime) < CACHE_DURATION_MS) {
                return cachedIndex;
            }

            try {
                String json = httpGet(repoUrl + "/index.json");
                cachedIndex = MarketplaceIndex.fromJson(json);
                lastFetchTime = now;

                // Write to disk cache
                Files.createDirectories(cacheDir);
                Path cacheFile = cacheDir.resolve("index.json");
                Path temp = cacheFile.resolveSibling("index.json.tmp");
                Files.writeString(temp, json, StandardCharsets.UTF_8);
                Files.move(temp, cacheFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

                return cachedIndex;
            } catch (Exception e) {
                OblivionClient.LOGGER.warn("Failed to fetch marketplace index: {}", e.getMessage());
                return loadDiskCache();
            }
        });
    }

    /**
     * Downloads all files for an addon to the target directory.
     */
    public CompletableFuture<Void> downloadAddon(MarketplaceEntry entry, Path targetDir) {
        return CompletableFuture.runAsync(() -> {
            try {
                Files.createDirectories(targetDir);

                // Download addon.json metadata
                String meta = httpGet(repoUrl + "/addons/" + entry.getId() + "/addon.json");
                Files.writeString(targetDir.resolve("addon.json"), meta, StandardCharsets.UTF_8);

                // Download each listed file
                for (String file : entry.getFiles()) {
                    if (file.equals("addon.json")) continue; // already downloaded
                    String content = httpGet(repoUrl + "/addons/" + entry.getId() + "/" + file);
                    Path filePath = targetDir.resolve(file);
                    Files.createDirectories(filePath.getParent());
                    Files.writeString(filePath, content, StandardCharsets.UTF_8);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to download addon '" + entry.getId() + "': " + e.getMessage(), e);
            }
        });
    }

    public void invalidateCache() {
        cachedIndex = null;
        lastFetchTime = 0;
    }

    private String httpGet(String urlStr) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);
        conn.setRequestProperty("User-Agent", "OblivionClient/" + OblivionClient.VERSION);

        int code = conn.getResponseCode();
        if (code != 200) {
            throw new IOException("HTTP " + code + " for " + urlStr);
        }

        try (var is = conn.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private MarketplaceIndex loadDiskCache() {
        Path cacheFile = cacheDir.resolve("index.json");
        if (!Files.exists(cacheFile)) return null;
        try {
            String json = Files.readString(cacheFile, StandardCharsets.UTF_8);
            return MarketplaceIndex.fromJson(json);
        } catch (Exception e) {
            OblivionClient.LOGGER.warn("Failed to load marketplace disk cache", e);
            return null;
        }
    }
}
