package tsp.headdb.core.command;

import org.bukkit.command.CommandSender;
import tsp.headdb.HeadDB;
import tsp.smartplugin.localization.TranslatableLocalization;

public abstract class HeadDBCommand {

    private static final TranslatableLocalization localization = HeadDB.getInstance().getLocalization();
    private final String name;
    private final String permission;

    public HeadDBCommand(String name, String permission) {
        this.name = name;
        this.permission = permission;
    }

    public abstract void handle(CommandSender sender, String[] args);

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public TranslatableLocalization getLocalization() {
        return localization;
    }

}