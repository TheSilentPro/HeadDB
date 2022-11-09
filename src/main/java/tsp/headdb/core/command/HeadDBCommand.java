package tsp.headdb.core.command;

import org.bukkit.command.CommandSender;
import tsp.headdb.HeadDB;
import tsp.smartplugin.localization.TranslatableLocalization;
import tsp.smartplugin.utils.Validate;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class HeadDBCommand {

    private static final TranslatableLocalization localization = HeadDB.getInstance().getLocalization();

    private final String name;
    private final String permission;
    private final Collection<String> completions;

    @ParametersAreNonnullByDefault
    public HeadDBCommand(String name, String permission, Collection<String> completions) {
        Validate.notNull(name, "Name can not be null!");
        Validate.notNull(permission, "Permission can not be null!");
        Validate.notNull(completions, "Completions can not be null!");

        this.name = name;
        this.permission = permission;
        this.completions = completions;
    }

    @ParametersAreNonnullByDefault
    public HeadDBCommand(String name, String permission) {
        this(name, permission, new ArrayList<>());
    }

    public abstract void handle(CommandSender sender, String[] args);

    public Collection<String> getCompletions() {
        return completions;
    }

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