package tsp.headdb.core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tsp.headdb.HeadDB;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.core.util.Localization;
import tsp.headdb.core.util.MenuSetup;

import java.util.*;

/**
 * @author TheSilentPro (Silent)
 */
public class CommandManager implements CommandExecutor, TabCompleter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private final Localization localization = HeadDB.getInstance().getLocalization();

    private final Set<HDBCommand> commands = new HashSet<>();

    public CommandManager init() {
        register(new CommandHelp());
        register(new CommandInfo());
        register(new CommandOpen());
        register(new CommandGive());
        register(new CommandSearch());
        register(new CommandUpdate());
        return this;
    }

    public void register(HDBCommand command) {
        this.commands.add(command);
        LOGGER.debug("Registered sub-command: {}", command.getName());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            for (HDBCommand sub : commands) {
                if (sub.matches(args[0])) {
                    if (!sender.hasPermission("headdb.command." + sub.getName())) {
                        localization.sendMessage(sender, "noPermission");
                        return true;
                    }
                    if (sub.isNoConsole() && !(sender instanceof Player)) {
                        localization.sendMessage(sender, "noConsole");
                        return true;
                    }
                    if (sub.waitUntilReady() && !HeadAPI.getDatabase().isReady()) {
                        localization.sendMessage(sender, "notReadyDatabase");
                        return true;
                    }
                    sub.handle(sender, Arrays.copyOfRange(args, 1, args.length));
                    return true;
                }
            }

            localization.sendMessage(sender, "invalidSubCommand", msg -> msg.replace("%name%", args[0]));
            return true;
        }

        // No sub command provided, open gui
        if (!(sender instanceof Player player)) {
            localization.sendMessage(sender, "noConsole");
            return true;
        }

        if (!HeadAPI.getDatabase().isReady()) {
            localization.sendMessage(sender, "notReadyDatabase");
            return true;
        }

        MenuSetup.mainGui.open(player);
        localization.sendMessage(player, "openDatabase");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return null;
        }

        if (args.length == 1) {
            return commands.stream().map(HDBCommand::getName).toList();
        }

        for (HDBCommand sub : commands) {
            if (sub.matches(args[0])) {
                if (!sender.hasPermission("headdb.command." + sub.getName())) {
                    return null;
                }
                if (sub.isNoConsole() && !(sender instanceof Player)) {
                    return null;
                }

                return sub.handleCompletions(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }

        return null;
    }

    public Set<HDBCommand> getCommands() {
        return Collections.unmodifiableSet(commands);
    }

}