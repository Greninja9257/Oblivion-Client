package dev.oblivion.client.command.commands;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.command.Command;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.util.ChatUtil;

import java.util.stream.Collectors;

public class ModulesCommand extends Command {

    public ModulesCommand() {
        super("modules", "Lists all modules", "mods");
    }

    @Override
    public void execute(String[] args) {
        var all = OblivionClient.get().moduleManager.getAll();
        String names = all.stream()
                .map(m -> (m.isEnabled() ? "\u00a7a" : "\u00a77") + m.name + "\u00a7r")
                .collect(Collectors.joining(", "));
        ChatUtil.info("Modules (" + all.size() + "): " + names);
    }

    @Override
    public String getUsage() {
        return "modules";
    }
}
