package dev.oblivion.client.command.commands;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.command.Command;
import dev.oblivion.client.util.ChatUtil;

public class PrefixCommand extends Command {

    public PrefixCommand() {
        super("prefix", "Sets the command prefix");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            ChatUtil.info("Current prefix: " + OblivionClient.get().getCommandManager().getPrefix());
            return;
        }
        OblivionClient.get().getCommandManager().setPrefix(args[0]);
        ChatUtil.success("Prefix set to: " + args[0]);
    }

    @Override
    public String getUsage() {
        return "prefix [char]";
    }
}
