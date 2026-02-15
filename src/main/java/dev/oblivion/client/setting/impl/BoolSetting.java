package dev.oblivion.client.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.oblivion.client.setting.Setting;

import java.util.function.Supplier;

public class BoolSetting extends Setting<Boolean> {
    public BoolSetting(String name, String description, boolean defaultValue, Supplier<Boolean> visible) {
        super(name, description, defaultValue, visible);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(get());
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json.isJsonPrimitive()) set(json.getAsBoolean());
    }

    public static class Builder {
        private String name = "";
        private String description = "";
        private boolean defaultValue = false;
        private Supplier<Boolean> visible;

        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String desc) { this.description = desc; return this; }
        public Builder defaultValue(boolean val) { this.defaultValue = val; return this; }
        public Builder visible(Supplier<Boolean> visible) { this.visible = visible; return this; }

        public BoolSetting build() {
            return new BoolSetting(name, description, defaultValue, visible);
        }
    }
}
