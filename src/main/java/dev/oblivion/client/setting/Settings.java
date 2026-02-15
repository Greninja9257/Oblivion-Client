package dev.oblivion.client.setting;

import java.util.ArrayList;
import java.util.List;

public class Settings {
    private final List<SettingGroup> groups = new ArrayList<>();
    private SettingGroup defaultGroup;

    public SettingGroup getDefaultGroup() {
        if (defaultGroup == null) {
            defaultGroup = createGroup("General");
        }
        return defaultGroup;
    }

    public SettingGroup createGroup(String name) {
        SettingGroup group = new SettingGroup(name);
        groups.add(group);
        return group;
    }

    public List<SettingGroup> getGroups() {
        return groups;
    }

    public List<Setting<?>> getAllSettings() {
        List<Setting<?>> all = new ArrayList<>();
        for (SettingGroup group : groups) {
            all.addAll(group.getSettings());
        }
        return all;
    }
}
