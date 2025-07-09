package com.github.thesilentpro.headdb.core.command;

import com.github.thesilentpro.headdb.core.HeadDB;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class HDBMainCommand implements CommandExecutor, TabCompleter {

    private static final Pattern USAGE_PATTERN = Pattern.compile(" ");
    private final HeadDB plugin;

    public HDBMainCommand(HeadDB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                plugin.getLocalization().sendMessage(sender, "noConsole");
                return true;
            }
            if (!(sender.hasPermission("headdb.command.open"))) {
                plugin.getLocalization().sendMessage(sender, "noPermission");
                return true;
            }
            plugin.getMenuManager().getMainMenu().open(player);
            plugin.getLocalization().sendMessage(sender, "command.open.opening", msg -> msg.replaceText(builder -> builder.matchLiteral("{category}").replacement("Main")));
            return true;
        }

        String sub = args[0];
        HDBSubCommand subCommand = plugin.getSubCommandManager().get(sub);
        if (subCommand == null) {
            plugin.getLocalization().sendMessage(sender, "invalidSubCommand", args);
            return true;
        }

        if (!sender.hasPermission("headdb.command." + subCommand.getName())) {
            plugin.getLocalization().sendMessage(sender, "noPermission");
            return true;
        }

        // Validate usage format
        if (subCommand.getUsage() != null) {
            String[] parts = USAGE_PATTERN.split(subCommand.getUsage());

            int required = 0;
            for (String part : parts) {
                if (!part.startsWith("[") && !part.endsWith("]")) { // Even if it doesn't have arrow brackets("<>"), assume the argument is required.
                    required++;
                }
            }

            if (args.length < required) {
                plugin.getLocalization().sendMessage(sender, "commandUsage", msg -> msg.replaceText(builder -> builder.matchLiteral("{usage}").replacement("/" + label + " " + sub + " " + subCommand.getUsage())));
                return true;
            }
        }

        subCommand.handle(sender, args);
        return true;
    }

    private static final List<String> INVALID_SUB_COMMAND_COMPLETION = Collections.singletonList("Invalid sub command!");

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return plugin.getSubCommandManager().getRealNames();
        }

        HDBSubCommand subCommand = plugin.getSubCommandManager().get(args[0]);
        if (subCommand == null) {
            return INVALID_SUB_COMMAND_COMPLETION; // no permission
        }

        if (!sender.hasPermission("headdb.command." + subCommand.getName())) {// no permission
            return null; // no permission
        }

        return subCommand.handleCompletions(sender, args);
    }

}