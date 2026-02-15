package dev.oblivion.client.command.commands;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.command.Command;
import dev.oblivion.client.util.ChatUtil;

public class FriendCommand extends Command {

    public FriendCommand() {
        super("friend", "Manages friend list", "f");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            ChatUtil.error("Usage: " + getUsage());
            return;
        }
        switch (args[0].toLowerCase()) {
            case "add" -> {
                if (args.length < 2) {
                    ChatUtil.error("Usage: friend add <name>");
                    return;
                }
                if (OblivionClient.get().friendManager.add(args[1])) {
                    ChatUtil.success("Added " + args[1] + " as a friend.");
                } else {
                    ChatUtil.error(args[1] + " is already a friend.");
                }
            }
            case "remove", "del" -> {
                if (args.length < 2) {
                    ChatUtil.error("Usage: friend remove <name>");
                    return;
                }
                if (OblivionClient.get().friendManager.remove(args[1])) {
                    ChatUtil.success("Removed " + args[1] + " from friends.");
                } else {
                    ChatUtil.error(args[1] + " is not a friend.");
                }
            }
            case "list" -> {
                var friends = OblivionClient.get().friendManager.all();
                if (friends.isEmpty()) {
                    ChatUtil.info("No friends added.");
                } else {
                    ChatUtil.info("Friends: " + String.join(", ", friends));
                }
            }
            default -> ChatUtil.error("Usage: " + getUsage());
        }
    }

    @Override
    public String getUsage() {
        return "friend <add|remove|list> [name]";
    }
}
