package dev.oblivion.client.command;

public abstract class Command {
    public final String name;
    public final String description;
    public final String[] aliases;

    protected Command(String name, String description, String... aliases) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
    }

    public abstract void execute(String[] args);

    public abstract String getUsage();
}
