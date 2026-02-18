package dev.oblivion.client.addon.marketplace;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class MarketplaceEntry {
    private final String id;
    private final String name;
    private final String author;
    private final String description;
    private final String version;
    private final String type;
    private final String category;
    private final List<String> files;
    private final long createdAt;
    private final long updatedAt;
    private final List<String> tags;

    public MarketplaceEntry(String id, String name, String author, String description,
                            String version, String type, String category, List<String> files,
                            long createdAt, long updatedAt, List<String> tags) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.description = description;
        this.version = version;
        this.type = type;
        this.category = category;
        this.files = files;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tags = tags;
    }

    public static MarketplaceEntry fromJson(JsonObject json) {
        String id = json.get("id").getAsString();
        String name = json.get("name").getAsString();
        String author = json.has("author") ? json.get("author").getAsString() : "Unknown";
        String description = json.has("description") ? json.get("description").getAsString() : "";
        String version = json.has("version") ? json.get("version").getAsString() : "1.0.0";
        String type = json.has("type") ? json.get("type").getAsString() : "java";
        String category = json.has("category") ? json.get("category").getAsString() : "MISC";
        long createdAt = json.has("createdAt") ? json.get("createdAt").getAsLong() : 0;
        long updatedAt = json.has("updatedAt") ? json.get("updatedAt").getAsLong() : 0;

        List<String> files = new ArrayList<>();
        if (json.has("files")) {
            JsonArray arr = json.getAsJsonArray("files");
            for (var e : arr) files.add(e.getAsString());
        }

        List<String> tags = new ArrayList<>();
        if (json.has("tags")) {
            JsonArray arr = json.getAsJsonArray("tags");
            for (var e : arr) tags.add(e.getAsString());
        }

        return new MarketplaceEntry(id, name, author, description, version, type, category, files, createdAt, updatedAt, tags);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public String getVersion() { return version; }
    public String getType() { return type; }
    public String getCategory() { return category; }
    public List<String> getFiles() { return files; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public List<String> getTags() { return tags; }
}
