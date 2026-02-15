package dev.oblivion.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.gui.screen.ClickGuiScreen;
import dev.oblivion.client.module.Module;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path dir = Path.of("run", "oblivion-client");
    private final Path profilesDir = dir.resolve("profiles");
    private final Path modulesFile = dir.resolve("modules.json");
    private final Path friendsFile = dir.resolve("friends.json");
    private final Path accountsFile = dir.resolve("accounts.json");
    private final Path proxiesFile = dir.resolve("proxies.json");
    private final Path clientFile = dir.resolve("client.json");
    private final Path hudFile = dir.resolve("hud.json");
    private final Path guiFile = dir.resolve("gui.json");

    private volatile String activeProfile = "default";

    public void load() {
        try {
            Files.createDirectories(dir);
            Files.createDirectories(profilesDir);

            loadModules(modulesFile);
            loadClient(clientFile);
            loadHud(hudFile);
            loadGui(guiFile);

            // If a named profile is selected, let it override base state files.
            if (!"default".equalsIgnoreCase(activeProfile)) {
                loadProfile(activeProfile);
            }

            OblivionClient.get().friendManager.load(friendsFile);
            OblivionClient.get().accountManager.load(accountsFile);
            OblivionClient.get().proxyManager.load(proxiesFile);
        } catch (Exception e) {
            OblivionClient.LOGGER.error("Failed to load config", e);
        }
    }

    public void save() {
        try {
            Files.createDirectories(dir);
            Files.createDirectories(profilesDir);

            saveModules(modulesFile);
            saveClient(clientFile);
            saveHud(hudFile);
            saveGui(guiFile);

            OblivionClient.get().friendManager.save(friendsFile);
            OblivionClient.get().accountManager.save(accountsFile);
            OblivionClient.get().proxyManager.save(proxiesFile);

            if (!"default".equalsIgnoreCase(activeProfile)) {
                saveProfile(activeProfile);
            }
        } catch (IOException e) {
            OblivionClient.LOGGER.error("Failed to save config", e);
        }
    }

    public void saveProfile(String name) {
        if (name == null || name.isBlank()) return;

        String normalized = name.trim();
        Path profileFile = profilesDir.resolve(normalized + ".json");
        JsonObject root = new JsonObject();
        root.addProperty("name", normalized);
        root.add("modules", buildModulesArray());
        root.add("client", buildClientJson());
        root.add("hud", OblivionClient.get().hudManager.toJson());
        root.add("gui", ClickGuiScreen.toStateJson());

        try {
            Files.createDirectories(profilesDir);
            atomicWrite(profileFile, GSON.toJson(root));
            activeProfile = normalized;
        } catch (IOException e) {
            OblivionClient.LOGGER.error("Failed to save profile {}", normalized, e);
        }
    }

    public void loadProfile(String name) {
        if (name == null || name.isBlank()) return;

        String normalized = name.trim();
        Path profileFile = profilesDir.resolve(normalized + ".json");
        if (!Files.exists(profileFile)) return;

        try {
            JsonObject root = JsonParser.parseString(Files.readString(profileFile, StandardCharsets.UTF_8)).getAsJsonObject();

            JsonArray modules = root.getAsJsonArray("modules");
            if (modules != null) {
                applyModulesArray(modules);
            }

            JsonObject client = root.getAsJsonObject("client");
            if (client != null) {
                applyClient(client);
            }

            JsonObject hud = root.getAsJsonObject("hud");
            if (hud != null) {
                OblivionClient.get().hudManager.fromJson(hud);
            }

            JsonObject gui = root.getAsJsonObject("gui");
            if (gui != null) {
                ClickGuiScreen.fromStateJson(gui);
            }

            activeProfile = normalized;
        } catch (Exception e) {
            OblivionClient.LOGGER.error("Failed to load profile {}", normalized, e);
        }
    }

    public String getActiveProfile() {
        return activeProfile;
    }

    private void loadModules(Path path) {
        if (!Files.exists(path)) return;

        try {
            JsonObject root = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonArray modules = root.getAsJsonArray("modules");
            if (modules != null) {
                applyModulesArray(modules);
            }
        } catch (Exception e) {
            OblivionClient.LOGGER.error("Failed to load modules config", e);
        }
    }

    private void saveModules(Path path) throws IOException {
        JsonObject root = new JsonObject();
        root.add("modules", buildModulesArray());
        atomicWrite(path, GSON.toJson(root));
    }

    private JsonArray buildModulesArray() {
        JsonArray modulesArray = new JsonArray();
        for (Module module : OblivionClient.get().moduleManager.getAll()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", module.name);
            obj.add("data", module.toJson());
            modulesArray.add(obj);
        }
        return modulesArray;
    }

    private void applyModulesArray(JsonArray modules) {
        for (int i = 0; i < modules.size(); i++) {
            try {
                JsonObject moduleJson = modules.get(i).getAsJsonObject();
                String name = moduleJson.get("name").getAsString();
                Module module = OblivionClient.get().moduleManager.get(name);
                if (module != null) {
                    module.fromJson(moduleJson.getAsJsonObject("data"));
                }
            } catch (Exception e) {
                OblivionClient.LOGGER.error("Failed to load module at index {}", i, e);
            }
        }
    }

    private void loadClient(Path path) {
        if (!Files.exists(path)) return;

        try {
            JsonObject client = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
            applyClient(client);
        } catch (Exception e) {
            OblivionClient.LOGGER.error("Failed to load client config", e);
        }
    }

    private void saveClient(Path path) throws IOException {
        atomicWrite(path, GSON.toJson(buildClientJson()));
    }

    private JsonObject buildClientJson() {
        JsonObject clientJson = new JsonObject();
        clientJson.addProperty("prefix", OblivionClient.get().commandManager.getPrefix());
        clientJson.addProperty("activeProfile", activeProfile);
        return clientJson;
    }

    private void applyClient(JsonObject client) {
        if (client.has("prefix")) {
            OblivionClient.get().commandManager.setPrefix(client.get("prefix").getAsString());
        }
        if (client.has("activeProfile")) {
            String configuredProfile = client.get("activeProfile").getAsString();
            if (!configuredProfile.isBlank()) {
                activeProfile = configuredProfile;
            }
        }
    }

    private void loadHud(Path path) {
        if (!Files.exists(path)) return;

        try {
            JsonObject hud = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
            OblivionClient.get().hudManager.fromJson(hud);
        } catch (Exception e) {
            OblivionClient.LOGGER.error("Failed to load HUD config", e);
        }
    }

    private void saveHud(Path path) throws IOException {
        atomicWrite(path, GSON.toJson(OblivionClient.get().hudManager.toJson()));
    }

    private void loadGui(Path path) {
        if (!Files.exists(path)) return;

        try {
            JsonObject gui = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
            ClickGuiScreen.fromStateJson(gui);
        } catch (Exception e) {
            OblivionClient.LOGGER.error("Failed to load GUI config", e);
        }
    }

    private void saveGui(Path path) throws IOException {
        atomicWrite(path, GSON.toJson(ClickGuiScreen.toStateJson()));
    }

    private void atomicWrite(Path target, String content) throws IOException {
        Path temp = target.resolveSibling(target.getFileName() + ".tmp");
        Files.writeString(temp, content, StandardCharsets.UTF_8);
        Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }
}
