package dev.oblivion.client.addon.marketplace;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.oblivion.client.OblivionClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class VoteStore {
    public enum Vote { UP, DOWN, NONE }

    private final Path file;
    private final Map<String, Vote> votes = new HashMap<>();

    public VoteStore(Path file) {
        this.file = file;
    }

    public void load() {
        if (!Files.exists(file)) return;
        try {
            String content = Files.readString(file, StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(content).getAsJsonObject();
            if (root.has("votes")) {
                JsonObject votesObj = root.getAsJsonObject("votes");
                for (var entry : votesObj.entrySet()) {
                    try {
                        votes.put(entry.getKey(), Vote.valueOf(entry.getValue().getAsString()));
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        } catch (Exception e) {
            OblivionClient.LOGGER.warn("Failed to load vote store", e);
        }
    }

    public void save() {
        try {
            Files.createDirectories(file.getParent());
            JsonObject root = new JsonObject();
            JsonObject votesObj = new JsonObject();
            for (var entry : votes.entrySet()) {
                votesObj.addProperty(entry.getKey(), entry.getValue().name());
            }
            root.add("votes", votesObj);

            Path temp = file.resolveSibling(file.getFileName() + ".tmp");
            Files.writeString(temp, root.toString(), StandardCharsets.UTF_8);
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            OblivionClient.LOGGER.warn("Failed to save vote store", e);
        }
    }

    public Vote getVote(String addonId) {
        return votes.getOrDefault(addonId, Vote.NONE);
    }

    public void setVote(String addonId, Vote vote) {
        if (vote == Vote.NONE) {
            votes.remove(addonId);
        } else {
            votes.put(addonId, vote);
        }
        save();
    }

    public int getScore(String addonId) {
        Vote v = getVote(addonId);
        return v == Vote.UP ? 1 : v == Vote.DOWN ? -1 : 0;
    }
}
