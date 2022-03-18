package tsp.headdb.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tsp.headdb.HeadDB;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.util.Localization;
import tsp.headdb.util.Utils;

public class HeadDBCommand implements CommandExecutor {

    private void handle(CommandSender sender, String[] args) {
        Localization localization = HeadDB.getInstance().getLocalization();
        if (args.length == 0) {
            if (!sender.hasPermission("headdb.open")) {
                Utils.sendMessage(sender, localization.getMessage("noPermission"));
                return;
            }
            if (!(sender instanceof Player player)) {
                Utils.sendMessage(sender, localization.getMessage("onlyPlayers"));
                return;
            }

            Utils.sendMessage(player, localization.getMessage("databaseOpen"));
            HeadAPI.openDatabase(player);
            return;
        }

        String sub = args[0];
        HeadSubCommand subCommand = null;
        if (sub.equalsIgnoreCase("info") || sub.equalsIgnoreCase("i")) {
            subCommand = new InfoCommand();
        } else if (sub.equalsIgnoreCase("reload") || sub.equalsIgnoreCase("r")) {
            subCommand = new ReloadCommand();
        } else if (sub.equalsIgnoreCase("search") || sub.equalsIgnoreCase("s")) {
            subCommand = new SearchCommand();
        } else if (sub.equalsIgnoreCase("tagsearch") || sub.equalsIgnoreCase("ts")) {
            subCommand = new TagSearchCommand();
        } else if (sub.equalsIgnoreCase("give") || sub.equalsIgnoreCase("g")) {
            subCommand = new GiveCommand();
        } else if (sub.equalsIgnoreCase("update") || sub.equalsIgnoreCase("u")) {
            subCommand = new UpdateCommand();
        }

        if (subCommand != null) {
            subCommand.handle(sender, args);
            return;
        }

        Utils.sendMessage(sender, " ");
        Utils.sendMessage(sender, "&c&lHeadDB &c- &5Commands");
        Utils.sendMessage(sender, "&7&oParameters:&c command &9(aliases)&c arguments... &7- Description");
        Utils.sendMessage(sender, "&7 > &c/hdb &7- Opens the database");
        Utils.sendMessage(sender, "&7 > &c/hdb info &9(i) &7- Plugin Information");
        Utils.sendMessage(sender, "&7 > &c/hdb reload &9(r) &7- Reloads the messages file");
        Utils.sendMessage(sender, "&7 > &c/hdb search &9(s) &c<name> &7- Search for heads matching a name");
        Utils.sendMessage(sender, "&7 > &c/hdb tagsearch &9(ts) &c<tag> &7- Search for heads matching a tag");
        Utils.sendMessage(sender, "&7 > &c/hdb update &9(u) &7- Forcefully update the database");
        Utils.sendMessage(sender, "&7 > &c/hdb give &9(g) &c<id> <player> &6[amount] &7- Give player a head");
        Utils.sendMessage(sender, " ");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        handle(sender, args);
        return true;
    }

}