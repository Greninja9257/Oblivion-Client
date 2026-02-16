package dev.oblivion.client.command.commands;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.command.Command;
import dev.oblivion.client.util.ChatUtil;
import net.minecraft.client.MinecraftClient;

import java.util.Arrays;

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
                var manager = OblivionClient.get().friendManager;
                var friends = manager.all();
                if (friends.isEmpty()) {
                    ChatUtil.info("No friends added.");
                } else {
                    ChatUtil.info("Friends:");
                    for (String line : manager.formatFriendStatusLines()) {
                        ChatUtil.info(" - " + line);
                    }
                }
            }
            case "msg", "message", "tell" -> {
                if (args.length < 3) {
                    ChatUtil.error("Usage: friend msg <name> <message>");
                    return;
                }

                String target = args[1];
                if (!OblivionClient.get().friendManager.isFriend(target)) {
                    ChatUtil.error(target + " is not in your friend list.");
                    return;
                }

                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.player == null || mc.getNetworkHandler() == null) {
                    ChatUtil.error("You must be connected to a server.");
                    return;
                }

                String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                mc.getNetworkHandler().sendChatCommand("msg " + target + " " + message);
                if (OblivionClient.get().friendManager.isOnline(target)) {
                    ChatUtil.success("Messaged " + target + ".");
                } else {
                    ChatUtil.warning(target + " appears offline on this server. Message still sent.");
                }
            }
            default -> ChatUtil.error("Usage: " + getUsage());
        }
    }

    @Override
    public String getUsage() {
        return "friend <add|remove|list|msg> [name] [message]";
    }
}
