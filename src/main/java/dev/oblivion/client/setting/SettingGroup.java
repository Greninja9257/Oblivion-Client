package dev.oblivion.client.setting;

import java.util.ArrayList;
import java.util.List;

public class SettingGroup {
    public final String name;
    private final List<Setting<?>> settings = new ArrayList<>();

    public SettingGroup(String name) {
        this.name = name;
    }

    public <T extends Setting<?>> T add(T setting) {
        settings.add(setting);
        return setting;
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }
}
