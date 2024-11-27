package tsp.headdb.core.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tsp.headdb.HeadDB;
import tsp.headdb.core.util.Localization;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author TheSilentPro (Silent)
 */
public abstract class HDBCommand {

    private static final Pattern DELIMITER = Pattern.compile(" ");
    protected final Localization localization = HeadDB.getInstance().getLocalization();
    
    private final String name;
    private final String parameters;
    private final String[] aliases;
    private final String description;
    private final boolean noConsole;

    public HDBCommand(String name, String parameters, String description, boolean noConsole, String... aliases) {
        this.name = name;
        this.aliases = aliases;
        this.noConsole = noConsole;
        this.description = description;

        if (parameters != null && !parameters.isEmpty()) {
            String[] params = DELIMITER.split(parameters);
            for (int i = 0; i < params.length; i++) {
                String param = params[i];
                if (param.startsWith("[") && param.endsWith("]")) {
                    params[i] = ChatColor.DARK_AQUA + param;
                } else {
                    params[i] = ChatColor.RED + param;
                }
            }
            this.parameters = String.join(" ", params);
        } else {
            this.parameters = "";
        }
    }

    public HDBCommand(String name, String description, boolean noConsole, String... aliases) {
        this(name, null, description, noConsole, aliases);
    }

    public boolean waitUntilReady() {
        return false;
    }

    public boolean matches(String raw) {
        if (raw.equalsIgnoreCase(name)) {
            return true;
        }

        for (String alias : aliases) {
            if (raw.equalsIgnoreCase(alias)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Handle sub command execution.
     * Arguments do not include the sub command.
     *
     * @param sender The sender of the command.
     * @param args The arguments.
     */
    public abstract void handle(CommandSender sender, String[] args);

    /**
     * Handle sub command execution.
     * Arguments do not include the sub command.
     *
     * @param sender The sender of the command.
     * @param args The arguments.
     */
    public List<String> handleCompletions(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    public String getName() {
        return name;
    }

    public String getParameters() {
        return parameters;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public boolean isNoConsole() {
        return noConsole;
    }

}