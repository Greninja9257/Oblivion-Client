package dev.oblivion.client.command.commands;

import dev.oblivion.client.OblivionClient;
import dev.oblivion.client.command.Command;
import dev.oblivion.client.util.ChatUtil;

public class AccountCommand extends Command {

    public AccountCommand() {
        super("account", "Manages offline accounts", "alt");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            ChatUtil.error("Usage: " + getUsage());
            return;
        }
        switch (args[0].toLowerCase()) {
            case "set", "use" -> {
                if (args.length < 2) {
                    ChatUtil.error("Usage: account set <name>");
                    return;
                }
                OblivionClient.get().accountManager.useOffline(args[1]);
                ChatUtil.success("Switched account to: " + args[1]);
            }
            case "list" -> {
                var accounts = OblivionClient.get().accountManager.getOfflineAccounts();
                if (accounts.isEmpty()) {
                    ChatUtil.info("No offline accounts saved.");
                } else {
                    ChatUtil.info("Offline accounts: " + String.join(", ", accounts));
                }
            }
            case "active" -> {
                String active = OblivionClient.get().accountManager.getActiveOfflineName();
                ChatUtil.info("Active offline account: " + (active == null ? "<none>" : active));
            }
            default -> ChatUtil.error("Usage: " + getUsage());
        }
    }

    @Override
    public String getUsage() {
        return "account <set|list|active> [name]";
    }
}
