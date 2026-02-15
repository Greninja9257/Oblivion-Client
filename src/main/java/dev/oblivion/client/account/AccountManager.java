package dev.oblivion.client.account;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.oblivion.client.mixin.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.session.Session;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountManager {
    private final List<String> offlineAccounts = new ArrayList<>();
    private String activeOfflineName;

    public void init() {
    }

    public boolean addOffline(String username) {
        if (username != null && !username.isBlank() && !offlineAccounts.contains(username)) {
            offlineAccounts.add(username);
            return true;
        }
        return false;
    }

    public List<String> getOfflineAccounts() {
        return Collections.unmodifiableList(offlineAccounts);
    }

    public void removeOffline(String username) {
        offlineAccounts.remove(username);
        if (username != null && username.equals(activeOfflineName)) {
            activeOfflineName = null;
        }
    }

    public void setActiveOfflineName(String username) {
        this.activeOfflineName = username;
    }

    public String getActiveOfflineName() {
        return activeOfflineName;
    }

    public boolean useOffline(String username) {
        if (username == null || username.isBlank()) return false;

        addOffline(username);
        setActiveOfflineName(username);

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return false;

        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
        Session session = new Session(
            username,
            uuid,
            "0",
            Optional.empty(),
            Optional.empty(),
            Session.AccountType.LEGACY
        );

        ((MinecraftClientAccessor) mc).setSession(session);

        if (mc.player != null && mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().getConnection().disconnect(Text.literal("Switched account to " + username));
            mc.disconnect(new MultiplayerScreen(new TitleScreen()));
        }

        return true;
    }

    public void load(Path file) throws IOException {
        offlineAccounts.clear();
        activeOfflineName = null;
        if (!Files.exists(file)) return;

        JsonObject root = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
        JsonArray accounts = root.getAsJsonArray("accounts");
        if (accounts != null) {
            accounts.forEach(e -> addOffline(e.getAsString()));
        }
        if (root.has("active")) {
            String active = root.get("active").getAsString();
            if (!active.isBlank()) setActiveOfflineName(active);
        }
    }

    public void save(Path file) throws IOException {
        JsonObject root = new JsonObject();
        JsonArray accounts = new JsonArray();
        for (String account : offlineAccounts) accounts.add(account);
        root.add("accounts", accounts);
        root.addProperty("active", activeOfflineName == null ? "" : activeOfflineName);
        Files.writeString(file, root.toString(), StandardCharsets.UTF_8);
    }
}
