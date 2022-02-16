package tsp.headdb.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tsp.headdb.HeadDB;
import tsp.headdb.api.Head;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.util.Localization;
import tsp.headdb.util.Utils;

import java.util.concurrent.TimeUnit;

// TODO: Cleaner way to handle this command
public class CommandHeadDB implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Localization localization = HeadDB.getInstance().getLocalization();
        if (args.length == 0) {
            if (!sender.hasPermission("headdb.open")) {
                Utils.sendMessage(sender, localization.getMessage("noPermission"));
                return true;
            }
            if (!(sender instanceof Player)) {
                Utils.sendMessage(sender, localization.getMessage("onlyPlayers"));
                return true;
            }
            Player player = (Player) sender;

            Utils.sendMessage(player, localization.getMessage("databaseOpen"));
            HeadAPI.openDatabase(player);
            return true;
        }
        String sub = args[0];

        if (sub.equalsIgnoreCase("info") || sub.equalsIgnoreCase("i")) {
            // These should not be customizable
            Utils.sendMessage(sender, "&7Running &cHeadDB - " + HeadDB.getInstance().getDescription().getVersion());
            Utils.sendMessage(sender, "&7Created by &c" + HeadDB.getInstance().getDescription().getAuthors());
            Utils.sendMessage(sender, "&7There are currently &c" + HeadAPI.getHeads().size() + " &7heads in the database.");
            return true;
        }

        if (sub.equalsIgnoreCase("search") || sub.equalsIgnoreCase("s")) {
            if (!sender.hasPermission("headdb.search")) {
                Utils.sendMessage(sender, localization.getMessage("noPermission"));
                return true;
            }
            if (!(sender instanceof Player)) {
                Utils.sendMessage(sender, localization.getMessage("onlyPlayers"));
                return true;
            }
            Player player = (Player) sender;

            if (args.length < 2) {
                Utils.sendMessage(player, "&c/hdb search <name>");
                return true;
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                builder.append(args[i]);
                if (i != args.length - 1) {
                    builder.append(" ");
                }
            }
            String name = builder.toString();
            Utils.sendMessage(sender, "&7Searching for &e" + name);
            HeadAPI.openSearchDatabase(player, name);
            return true;
        }

        if (sub.equalsIgnoreCase("tagsearch") || sub.equalsIgnoreCase("ts")) {
            if (!sender.hasPermission("headdb.tagsearch")) {
                Utils.sendMessage(sender, localization.getMessage("noPermission"));
                return true;
            }
            if (args.length < 2) {
                Utils.sendMessage(sender, "&c/hdb tagsearch <tag>");
                return true;
            }
            if (!(sender instanceof Player)) {
                Utils.sendMessage(sender, localization.getMessage("onlyPlayers"));
                return true;
            }
            Player player = (Player) sender;

            String tag = args[1];
            Utils.sendMessage(sender, "&7Searching for heads with tag &e" + tag);
            HeadAPI.openTagSearchDatabase(player, tag);
            return true;
        }

        if (sub.equalsIgnoreCase("give") || sub.equalsIgnoreCase("g")) {
            if (!sender.hasPermission("headdb.give")) {
                Utils.sendMessage(sender, localization.getMessage("noPermission"));
                return true;
            }
            if (args.length < 3) {
                Utils.sendMessage(sender, "&c/hdb give <id> <player> &6[amount]");
                return true;
            }
            try {
                int id = Integer.parseInt(args[1]);
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    Utils.sendMessage(sender, localization.getMessage("invalidPlayer"));
                    return true;
                }

                int amount = 1;
                if (args.length > 3) {
                    amount = Integer.parseInt(args[3]);
                }

                Head head = HeadAPI.getHeadByID(id);
                if (head == null) {
                    Utils.sendMessage(sender, "&cCould not find head with id &e" + id);
                    return true;
                }
                ItemStack item = head.getItemStack();
                item.setAmount(amount);
                target.getInventory().addItem(item);
                Utils.sendMessage(sender, "&7Gave &6" + target.getName() + " &ex" + amount + " " + head.getName());
                return true;
            } catch (NumberFormatException nfe) {
                Utils.sendMessage(sender, "&cID/Amount must be a number!");
                return true;
            }
        }

        if (sub.equalsIgnoreCase("update") || sub.equalsIgnoreCase("u")) {
            if (!sender.hasPermission("headdb.update")) {
                Utils.sendMessage(sender, "&cNo permission!");
                return true;
            }

            Utils.sendMessage(sender, "&7Updating...");
            long start = System.currentTimeMillis();
            HeadAPI.getDatabase().updateAsync(heads -> {
                Utils.sendMessage(sender, "&7Done! Took: &a" + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start) + " &7seconds");
                Utils.sendMessage(sender, "&7There are &a" + HeadAPI.getHeads().size() + " &7heads in the database!");
            });
            return true;
        }

        Utils.sendMessage(sender, " ");
        Utils.sendMessage(sender, "&c&lHeadDB &c- &5Commands");
        Utils.sendMessage(sender, "&7&oParameters:&c command &9(aliases)&c arguments... &7- Description");
        Utils.sendMessage(sender, "&7 > &c/hdb &7- Opens the database");
        Utils.sendMessage(sender, "&7 > &c/hdb info &9(i) &7- Plugin Information");
        Utils.sendMessage(sender, "&7 > &c/hdb search &9(s) &c<name> &7- Search for heads matching a name");
        Utils.sendMessage(sender, "&7 > &c/hdb tagsearch &9(ts) &c<tag> &7- Search for heads matching a tag");
        Utils.sendMessage(sender, "&7 > &c/hdb update &9(u) &7- Forcefully update the database");
        Utils.sendMessage(sender, "&7 > &c/hdb give &9(g) &c<id> <player> &6[amount] &7- Give player a head");
        Utils.sendMessage(sender, " ");
        return true;
    }

}