package dev.oblivion.client.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.oblivion.client.setting.Setting;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

public class KeybindSetting extends Setting<Integer> {
    public KeybindSetting(String name, String description, int defaultValue, Supplier<Boolean> visible) {
        super(name, description, defaultValue, visible);
    }

    public boolean isUnbound() {
        return get() == GLFW.GLFW_KEY_UNKNOWN;
    }

    public String getKeyName() {
        if (isUnbound()) return "None";
        String name = GLFW.glfwGetKeyName(get(), 0);
        return name != null ? name.toUpperCase() : "KEY_" + get();
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(get());
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json.isJsonPrimitive()) set(json.getAsInt());
    }

    public static class Builder {
        private String name = "Keybind";
        private String description = "";
        private int defaultValue = GLFW.GLFW_KEY_UNKNOWN;
        private Supplier<Boolean> visible;

        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String desc) { this.description = desc; return this; }
        public Builder defaultValue(int val) { this.defaultValue = val; return this; }
        public Builder visible(Supplier<Boolean> visible) { this.visible = visible; return this; }

        public KeybindSetting build() {
            return new KeybindSetting(name, description, defaultValue, visible);
        }
    }
}
