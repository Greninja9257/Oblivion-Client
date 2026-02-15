package dev.oblivion.client.module;

import com.google.gson.JsonObject;
import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.setting.Setting;
import dev.oblivion.client.setting.Settings;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public abstract class Module {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    public final String name;
    public final String description;
    public final Category category;
    public final Settings settings = new Settings();

    private volatile boolean enabled;
    private int keybind = GLFW.GLFW_KEY_UNKNOWN;
    private boolean drawn = true;

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public synchronized void toggle() {
        if (enabled) disable();
        else enable();
    }

    public synchronized void enable() {
        if (enabled) return;
        enabled = true;
        OblivionClient.get().eventBus.register(this);
        onEnable();
        if (mc.player != null) {
            OblivionClient.get().notificationManager.show(name + " enabled", NotificationType.ENABLED);
        }
    }

    public synchronized void disable() {
        if (!enabled) return;
        enabled = false;
        OblivionClient.get().eventBus.unregister(this);
        onDisable();
        if (mc.player != null) {
            OblivionClient.get().notificationManager.show(name + " disabled", NotificationType.DISABLED);
        }
    }

    public enum NotificationType {
        ENABLED, DISABLED, INFO
    }

    protected void onEnable() {}
    protected void onDisable() {}

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (enabled) enable();
        else disable();
    }

    public int getKeybind() {
        return keybind;
    }

    public void setKeybind(int keybind) {
        this.keybind = keybind;
    }

    public boolean isDrawn() {
        return drawn;
    }

    public void setDrawn(boolean drawn) {
        this.drawn = drawn;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("enabled", enabled);
        json.addProperty("keybind", keybind);
        json.addProperty("drawn", drawn);

        JsonObject settingsJson = new JsonObject();
        for (Setting<?> setting : settings.getAllSettings()) {
            settingsJson.add(setting.name, setting.toJson());
        }
        json.add("settings", settingsJson);

        return json;
    }

    public void fromJson(JsonObject json) {
        if (json.has("keybind")) keybind = json.get("keybind").getAsInt();
        if (json.has("drawn")) drawn = json.get("drawn").getAsBoolean();

        if (json.has("settings")) {
            JsonObject settingsJson = json.getAsJsonObject("settings");
            for (Setting<?> setting : settings.getAllSettings()) {
                if (settingsJson.has(setting.name)) {
                    setting.fromJson(settingsJson.get(setting.name));
                }
            }
        }

        if (json.has("enabled") && json.get("enabled").getAsBoolean()) {
            enable();
        }
    }
}
