package dev.oblivion.client.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.oblivion.client.setting.Setting;

import java.util.function.Supplier;

public class IntSetting extends Setting<Integer> {
    public final int min;
    public final int max;

    public IntSetting(String name, String description, int defaultValue, int min, int max, Supplier<Boolean> visible) {
        super(name, description, defaultValue, visible);
        this.min = min;
        this.max = max;
    }

    @Override
    public void set(Integer value) {
        super.set(Math.max(min, Math.min(max, value)));
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
        private String name = "";
        private String description = "";
        private int defaultValue = 0;
        private int min = 0;
        private int max = 100;
        private Supplier<Boolean> visible;

        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String desc) { this.description = desc; return this; }
        public Builder defaultValue(int val) { this.defaultValue = val; return this; }
        public Builder min(int min) { this.min = min; return this; }
        public Builder max(int max) { this.max = max; return this; }
        public Builder range(int min, int max) { this.min = min; this.max = max; return this; }
        public Builder visible(Supplier<Boolean> visible) { this.visible = visible; return this; }

        public IntSetting build() {
            return new IntSetting(name, description, defaultValue, min, max, visible);
        }
    }
}
