package dev.oblivion.client.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.oblivion.client.setting.Setting;

import java.util.function.Supplier;

public class StringSetting extends Setting<String> {
    public StringSetting(String name, String description, String defaultValue, Supplier<Boolean> visible) {
        super(name, description, defaultValue, visible);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(get());
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json.isJsonPrimitive()) set(json.getAsString());
    }

    public static class Builder {
        private String name = "";
        private String description = "";
        private String defaultValue = "";
        private Supplier<Boolean> visible;

        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String desc) { this.description = desc; return this; }
        public Builder defaultValue(String val) { this.defaultValue = val; return this; }
        public Builder visible(Supplier<Boolean> visible) { this.visible = visible; return this; }

        public StringSetting build() {
            return new StringSetting(name, description, defaultValue, visible);
        }
    }
}
