package dev.oblivion.client.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.oblivion.client.setting.Setting;

import java.util.function.Supplier;

public class DoubleSetting extends Setting<Double> {
    public final double min;
    public final double max;

    public DoubleSetting(String name, String description, double defaultValue, double min, double max, Supplier<Boolean> visible) {
        super(name, description, defaultValue, visible);
        this.min = min;
        this.max = max;
    }

    @Override
    public void set(Double value) {
        super.set(Math.max(min, Math.min(max, value)));
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(get());
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json.isJsonPrimitive()) set(json.getAsDouble());
    }

    public static class Builder {
        private String name = "";
        private String description = "";
        private double defaultValue = 0;
        private double min = 0;
        private double max = 100;
        private Supplier<Boolean> visible;

        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String desc) { this.description = desc; return this; }
        public Builder defaultValue(double val) { this.defaultValue = val; return this; }
        public Builder min(double min) { this.min = min; return this; }
        public Builder max(double max) { this.max = max; return this; }
        public Builder range(double min, double max) { this.min = min; this.max = max; return this; }
        public Builder visible(Supplier<Boolean> visible) { this.visible = visible; return this; }

        public DoubleSetting build() {
            return new DoubleSetting(name, description, defaultValue, min, max, visible);
        }
    }
}
