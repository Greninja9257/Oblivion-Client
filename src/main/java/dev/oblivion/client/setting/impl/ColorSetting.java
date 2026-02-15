package dev.oblivion.client.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.oblivion.client.setting.Setting;

import java.awt.*;
import java.util.function.Supplier;

public class ColorSetting extends Setting<Integer> {
    public ColorSetting(String name, String description, int defaultValue, Supplier<Boolean> visible) {
        super(name, description, defaultValue, visible);
    }

    public int getRed() { return (get() >> 16) & 0xFF; }
    public int getGreen() { return (get() >> 8) & 0xFF; }
    public int getBlue() { return get() & 0xFF; }
    public int getAlpha() { return (get() >> 24) & 0xFF; }

    public void setRGBA(int r, int g, int b, int a) {
        set((a << 24) | (r << 16) | (g << 8) | b);
    }

    public static int rgba(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(Integer.toHexString(get()));
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json.isJsonPrimitive()) {
            try {
                set((int) Long.parseLong(json.getAsString(), 16));
            } catch (NumberFormatException ignored) {}
        }
    }

    public static class Builder {
        private String name = "";
        private String description = "";
        private int defaultValue = 0xFFFFFFFF;
        private Supplier<Boolean> visible;

        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String desc) { this.description = desc; return this; }
        public Builder defaultValue(int val) { this.defaultValue = val; return this; }
        public Builder defaultValue(int r, int g, int b, int a) { this.defaultValue = rgba(r, g, b, a); return this; }
        public Builder visible(Supplier<Boolean> visible) { this.visible = visible; return this; }

        public ColorSetting build() {
            return new ColorSetting(name, description, defaultValue, visible);
        }
    }
}
