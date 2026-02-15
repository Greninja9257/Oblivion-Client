package dev.oblivion.client.setting;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Setting<T> {
    public final String name;
    public final String description;
    protected T value;
    protected final T defaultValue;
    protected Supplier<Boolean> visible;
    protected final List<Consumer<T>> onChanged = new ArrayList<>();

    protected Setting(String name, String description, T defaultValue, Supplier<Boolean> visible) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.visible = visible;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        T old = this.value;
        this.value = value;
        if (old == null || !old.equals(value)) {
            onChanged.forEach(c -> c.accept(value));
        }
    }

    public T getDefault() {
        return defaultValue;
    }

    public void reset() {
        set(defaultValue);
    }

    public boolean isVisible() {
        return visible == null || visible.get();
    }

    public void onChanged(Consumer<T> listener) {
        onChanged.add(listener);
    }

    public abstract JsonElement toJson();
    public abstract void fromJson(JsonElement json);
}
