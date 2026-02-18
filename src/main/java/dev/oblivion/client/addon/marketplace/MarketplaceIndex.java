package dev.oblivion.client.addon.marketplace;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class MarketplaceIndex {
    private final List<MarketplaceEntry> entries;
    private final long timestamp;

    public MarketplaceIndex(List<MarketplaceEntry> entries, long timestamp) {
        this.entries = entries;
        this.timestamp = timestamp;
    }

    public static MarketplaceIndex fromJson(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        long timestamp = root.has("timestamp") ? root.get("timestamp").getAsLong() : 0;
        List<MarketplaceEntry> entries = new ArrayList<>();
        if (root.has("addons")) {
            JsonArray addons = root.getAsJsonArray("addons");
            for (var e : addons) {
                entries.add(MarketplaceEntry.fromJson(e.getAsJsonObject()));
            }
        }
        return new MarketplaceIndex(entries, timestamp);
    }

    public List<MarketplaceEntry> getEntries() { return entries; }
    public long getTimestamp() { return timestamp; }
}
