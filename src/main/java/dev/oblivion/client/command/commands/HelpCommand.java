package dev.oblivion.client.command.commands;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.command.Command;
import dev.oblivion.client.util.ChatUtil;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "Shows all available commands", "h", "?");
    }

    @Override
    public void execute(String[] args) {
        String prefix = OblivionClient.get().getCommandManager().getPrefix();
        ChatUtil.info("--- Oblivion Commands ---");
        for (Command cmd : OblivionClient.get().getCommandManager().getCommands()) {
            StringBuilder sb = new StringBuilder();
            sb.append(prefix).append(cmd.name);
            if (cmd.aliases.length > 0) {
                sb.append(" (");
                for (int i = 0; i < cmd.aliases.length; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(cmd.aliases[i]);
                }
                sb.append(")");
            }
            sb.append(" - ").append(cmd.description);
            ChatUtil.info(sb.toString());
        }
    }

    @Override
    public String getUsage() {
        return "help";
    }
}
