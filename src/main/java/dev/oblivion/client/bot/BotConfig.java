package dev.oblivion.client.bot;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class BotConfig {

    public record AutoRegister(String password, String registerFormat, String loginFormat) {}

    private final AtomicReference<AutoRegister> autoRegister = new AtomicReference<>(null);
    private final CopyOnWriteArrayList<String> pendingNames = new CopyOnWriteArrayList<>();

    public AutoRegister getAutoRegister() {
        return autoRegister.get();
    }

    public void setAutoRegister(AutoRegister ar) {
        autoRegister.set(ar);
    }

    public List<String> consumePendingNames() {
        List<String> names = List.copyOf(pendingNames);
        pendingNames.clear();
        return names;
    }

    public void setPendingNames(List<String> names) {
        pendingNames.clear();
        pendingNames.addAll(names);
    }
}
