package dev.oblivion.client.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.oblivion.client.setting.Setting;

import java.util.function.Supplier;

public class EnumSetting<E extends Enum<E>> extends Setting<E> {
    private final Class<E> enumClass;

    public EnumSetting(String name, String description, E defaultValue, Supplier<Boolean> visible) {
        super(name, description, defaultValue, visible);
        this.enumClass = defaultValue.getDeclaringClass();
    }

    public E[] getValues() {
        return enumClass.getEnumConstants();
    }

    public void cycle() {
        E[] values = getValues();
        int next = (get().ordinal() + 1) % values.length;
        set(values[next]);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(get().name());
    }

    @Override
    public void fromJson(JsonElement json) {
        if (json.isJsonPrimitive()) {
            try {
                set(Enum.valueOf(enumClass, json.getAsString()));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public static class Builder<E extends Enum<E>> {
        private String name = "";
        private String description = "";
        private E defaultValue;
        private Supplier<Boolean> visible;

        public Builder<E> name(String name) { this.name = name; return this; }
        public Builder<E> description(String desc) { this.description = desc; return this; }
        public Builder<E> defaultValue(E val) { this.defaultValue = val; return this; }
        public Builder<E> visible(Supplier<Boolean> visible) { this.visible = visible; return this; }

        public EnumSetting<E> build() {
            return new EnumSetting<>(name, description, defaultValue, visible);
        }
    }
}
