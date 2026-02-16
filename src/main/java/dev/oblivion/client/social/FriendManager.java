package dev.oblivion.client.social;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.event.EventHandler;
import dev.oblivion.client.event.events.TickEvent;
import dev.oblivion.client.util.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class FriendManager {
    private final Set<String> friends = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    private final Set<String> onlineFriends = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, String> lastKnownServer = new java.util.HashMap<>();
    private String trackedServerKey = "";
    private boolean initializedForServer = false;

    public void init() {
        OblivionClient.get().eventBus.register(this);
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

    public Set<String> online() {
        return Collections.unmodifiableSet(onlineFriends);
    }

    public boolean isOnline(String name) {
        return name != null && onlineFriends.contains(name);
    }

    public String getCurrentServerLabel() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return "unknown";
        if (mc.isInSingleplayer()) return "singleplayer";
        if (mc.getCurrentServerEntry() != null && mc.getCurrentServerEntry().address != null) {
            return mc.getCurrentServerEntry().address;
        }
        return "unknown";
    }

    public String getServerForFriend(String name) {
        if (name == null) return "unknown";
        if (isOnline(name)) return getCurrentServerLabel();
        return lastKnownServer.getOrDefault(name, "offline");
    }

    public Set<String> formatFriendStatusLines() {
        Set<String> lines = new LinkedHashSet<>();
        for (String friend : friends) {
            String status = isOnline(friend) ? "online" : "offline";
            String server = getServerForFriend(friend);
            lines.add(friend + " - " + status + " (" + server + ")");
        }
        return lines;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.player == null || mc.getNetworkHandler() == null) {
            onlineFriends.clear();
            trackedServerKey = "";
            initializedForServer = false;
            return;
        }

        String serverKey = getCurrentServerLabel();
        if (!serverKey.equals(trackedServerKey)) {
            trackedServerKey = serverKey;
            initializedForServer = false;
            onlineFriends.clear();
        }

        Set<String> currentOnlineFriends = mc.getNetworkHandler().getPlayerList().stream()
            .map(PlayerListEntry::getProfile)
            .filter(p -> p != null && p.getName() != null)
            .map(com.mojang.authlib.GameProfile::getName)
            .filter(this::isFriend)
            .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));

        for (String friend : currentOnlineFriends) {
            lastKnownServer.put(friend, serverKey);
        }

        if (!initializedForServer) {
            onlineFriends.clear();
            onlineFriends.addAll(currentOnlineFriends);
            initializedForServer = true;
            return;
        }

        for (String friend : currentOnlineFriends) {
            if (!onlineFriends.contains(friend)) {
                ChatUtil.success("Friend joined: " + friend + " on " + serverKey);
            }
        }

        for (String friend : onlineFriends) {
            if (!currentOnlineFriends.contains(friend)) {
                ChatUtil.warning("Friend left: " + friend + " from " + serverKey);
            }
        }

        onlineFriends.clear();
        onlineFriends.addAll(currentOnlineFriends);
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
