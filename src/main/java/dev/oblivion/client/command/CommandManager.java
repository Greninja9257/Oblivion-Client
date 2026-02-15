package dev.oblivion.client.command;

import dev.oblivion.client.command.commands.*;
import dev.oblivion.client.util.ChatUtil;

import java.util.*;

public class CommandManager {
    private String prefix = ".";
    private final Map<String, Command> commands = new LinkedHashMap<>();
    private final List<Command> commandList = new ArrayList<>();

    public void init() {
        register(new HelpCommand());
        register(new ModulesCommand());
        register(new ToggleCommand());
        register(new BindCommand());
        register(new PrefixCommand());
        register(new AccountCommand());
        register(new FriendCommand());
    }

    public void register(Command command) {
        commandList.add(command);
        commands.put(command.name.toLowerCase(), command);
        for (String alias : command.aliases) {
            commands.put(alias.toLowerCase(), command);
        }
    }

    public boolean dispatch(String message) {
        if (message == null || !message.startsWith(prefix)) return false;

        String content = message.substring(prefix.length()).trim();
        if (content.isEmpty()) return true;

        String[] parts = content.split("\\s+");
        String cmdName = parts[0].toLowerCase();

        Command command = commands.get(cmdName);
        if (command == null) {
            ChatUtil.error("Unknown command: " + cmdName + ". Type " + prefix + "help for a list.");
            return true;
        }

        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        try {
            command.execute(args);
        } catch (Exception e) {
            ChatUtil.error("Error executing command: " + e.getMessage());
        }

        return true;
    }

    public Collection<Command> getCommands() {
        return Collections.unmodifiableList(commandList);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        if (prefix != null && !prefix.isBlank()) this.prefix = prefix;
    }
}
