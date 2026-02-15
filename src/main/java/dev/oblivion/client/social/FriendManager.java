package dev.oblivion.client.social;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class FriendManager {
    private final Set<String> friends = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    public void init() {
    }

    public boolean isFriend(String name) {
        return name != null && friends.contains(name);
    }

    public boolean add(String name) {
        return name != null && !name.isBlank() && friends.add(name);
    }

    public boolean remove(String name) {
        return name != null && friends.remove(name);
    }

    public Set<String> all() {
        return Collections.unmodifiableSet(friends);
    }

    public void load(Path file) throws IOException {
        friends.clear();
        if (!Files.exists(file)) return;
        JsonArray arr = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonArray();
        arr.forEach(e -> friends.add(e.getAsString()));
    }

    public void save(Path file) throws IOException {
        JsonArray arr = new JsonArray();
        for (String friend : friends) arr.add(friend);
        Files.writeString(file, arr.toString(), StandardCharsets.UTF_8);
    }
}
