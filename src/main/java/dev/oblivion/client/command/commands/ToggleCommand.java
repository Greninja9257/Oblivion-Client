package dev.oblivion.client.command.commands;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.command.Command;
import dev.oblivion.client.module.Module;
import dev.oblivion.client.util.ChatUtil;

public class ToggleCommand extends Command {

    public ToggleCommand() {
        super("toggle", "Toggles a module on/off", "t");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            ChatUtil.error("Usage: " + getUsage());
            return;
        }
        Module module = OblivionClient.get().moduleManager.get(args[0]);
        if (module == null) {
            ChatUtil.error("Unknown module: " + args[0]);
            return;
        }
        module.toggle();
    }

    @Override
    public String getUsage() {
        return "toggle <module>";
    }
}
