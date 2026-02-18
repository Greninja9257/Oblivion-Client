package dev.oblivion.client.addon;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.oblivion.client.module.Category;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.setting.impl.BoolSetting;
import dev.oblivion.client.setting.impl.DoubleSetting;
import dev.oblivion.client.setting.impl.IntSetting;
import dev.oblivion.client.setting.impl.StringSetting;

/**
 * Runtime module created from a JSON config addon definition.
 * Supports declarative settings (bool, int, double, string).
 */
public class AddonModule extends Module {
    private final Addon addon;

    public AddonModule(Addon addon, JsonObject config) {
        super(
            config.has("name") ? config.get("name").getAsString() : addon.getName(),
            config.has("description") ? config.get("description").getAsString() : addon.getDescription(),
            addon.getCategory()
        );
        this.addon = addon;

        if (config.has("settings")) {
            parseSettings(config.getAsJsonArray("settings"));
        }
    }

    private void parseSettings(JsonArray settingsArr) {
        for (var element : settingsArr) {
            if (!element.isJsonObject()) continue;
            JsonObject s = element.getAsJsonObject();
            String type = s.has("type") ? s.get("type").getAsString() : "";
            String name = s.has("name") ? s.get("name").getAsString() : "Setting";
            String desc = s.has("description") ? s.get("description").getAsString() : "";

            switch (type) {
                case "bool" -> {
                    boolean def = s.has("default") && s.get("default").getAsBoolean();
                    settings.getDefaultGroup().add(
                        new BoolSetting.Builder().name(name).description(desc).defaultValue(def).build()
                    );
                }
                case "int" -> {
                    int def = s.has("default") ? s.get("default").getAsInt() : 0;
                    int min = s.has("min") ? s.get("min").getAsInt() : 0;
                    int max = s.has("max") ? s.get("max").getAsInt() : 100;
                    settings.getDefaultGroup().add(
                        new IntSetting.Builder().name(name).description(desc).defaultValue(def).range(min, max).build()
                    );
                }
                case "double" -> {
                    double def = s.has("default") ? s.get("default").getAsDouble() : 0;
                    double min = s.has("min") ? s.get("min").getAsDouble() : 0;
                    double max = s.has("max") ? s.get("max").getAsDouble() : 100;
                    settings.getDefaultGroup().add(
                        new DoubleSetting.Builder().name(name).description(desc).defaultValue(def).range(min, max).build()
                    );
                }
                case "string" -> {
                    String def = s.has("default") ? s.get("default").getAsString() : "";
                    settings.getDefaultGroup().add(
                        new StringSetting.Builder().name(name).description(desc).defaultValue(def).build()
                    );
                }
            }
        }
    }

    public Addon getAddon() { return addon; }
}
