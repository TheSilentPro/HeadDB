package tsp.headdb.core.command;

import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class HeadDBCommand {

    private final String name;
    private final String permission;
    private final String[] aliases;

    public HeadDBCommand(String name, String permission, @Nullable String[] aliases) {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
    }

    public HeadDBCommand(String name, String permission) {
        this(name, permission, null);
    }

    public abstract void handle(CommandSender sender, String[] args);

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public Optional<String[]> getAliases() {
        return Optional.ofNullable(aliases);
    }

}