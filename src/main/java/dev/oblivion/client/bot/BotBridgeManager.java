package dev.oblivion.client.bot;

import com.google.gson.JsonObject;
import dev.oblivion.client.OblivionClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.Random;

public class BotBridgeManager {
    public static final String DEFAULT_ENDPOINT = "http://127.0.0.1:3099/api/bots/command";

    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(3))
        .build();

    private final Random random = new Random();
    private final BotRuntimeManager runtimeManager = new BotRuntimeManager();

    public void init() {
        runtimeManager.start();
    }

    public void shutdown() {
        runtimeManager.shutdown();
    }

    public boolean sendCommand(String endpoint, String apiToken, JsonObject payload) {
        try {
            String safeEndpoint = (endpoint == null || endpoint.isBlank()) ? DEFAULT_ENDPOINT : endpoint.trim();
            if (isManagedLocalEndpoint(safeEndpoint)) {
                runtimeManager.ensureReady();
            }

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(safeEndpoint))
                .timeout(Duration.ofSeconds(6))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8));

            if (apiToken != null && !apiToken.isBlank()) {
                requestBuilder.header("Authorization", "Bearer " + apiToken.trim());
            }

            HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            int status = response.statusCode();
            if (status >= 200 && status < 300) {
                return true;
            }

            OblivionClient.LOGGER.warn("Bot bridge request failed: status={} body={}", status, response.body());
            return false;
        } catch (Exception e) {
            OblivionClient.LOGGER.warn("Failed to reach bot bridge endpoint {}", endpoint, e);
            return false;
        }
    }

    public String randomName(String prefix, int randomLength) {
        String safePrefix = (prefix == null || prefix.isBlank()) ? "bot" : prefix;
        int len = Math.max(3, randomLength);
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder sb = new StringBuilder();
        sb.append(safePrefix.toLowerCase(Locale.ROOT));
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    private boolean isManagedLocalEndpoint(String endpoint) {
        try {
            URI uri = URI.create(endpoint);
            if (uri.getHost() == null) return false;
            String host = uri.getHost().toLowerCase(Locale.ROOT);
            if (!host.equals("127.0.0.1") && !host.equals("localhost")) return false;
            int port = uri.getPort() == -1 ? 80 : uri.getPort();
            return port == 3099;
        } catch (Exception ignored) {
            return false;
        }
    }
}
