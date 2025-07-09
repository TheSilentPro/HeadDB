package com.github.thesilentpro.headdb.core.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class HDBSubCommand {

    private final String name;
    private final String[] aliases;
    private final String description;
    private final String usage;

    public HDBSubCommand(String name, String description, String usage, String... aliases) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
        this.usage = usage;
    }

    public abstract void handle(CommandSender sender, String[] args);

    // Starts from sub-command(index 0)
    @Nullable
    public List<String> handleCompletions(CommandSender sender, String[] args) {
        return null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public String[] getAliases() {
        return aliases;
    }

}