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
import tsp.headdb.inventory.InventoryUtils;
import tsp.headdb.util.Utils;

public class Command_headdb implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            if (!sender.hasPermission("headdb.open")) {
                Utils.sendMessage(sender, "&cNo permission!");
                return true;
            }
            if (!(sender instanceof Player)) {
                Utils.sendMessage(sender, "&cOnly players may open the database.");
                return true;
            }
            Player player = (Player) sender;

            Utils.sendMessage(player, "Opening &cHead Database");
            InventoryUtils.openDatabase(player);
            return true;
        }
        String sub = args[0];

        if (sub.equalsIgnoreCase("info") || sub.equalsIgnoreCase("i")) {
            Utils.sendMessage(sender, "Running &cHeadDB v" + HeadDB.getInstance().getDescription().getVersion());
            Utils.sendMessage(sender, "Created by &c" + HeadDB.getInstance().getDescription().getAuthors());
            Utils.sendMessage(sender, "There are currently &c" + HeadAPI.getHeads().size() + " &7heads in the database.");
            return true;
        }

        if (sub.equalsIgnoreCase("search") || sub.equalsIgnoreCase("s")) {
            if (!sender.hasPermission("headdb.search")) {
                Utils.sendMessage(sender, "&cNo permission!");
                return true;
            }
            if (args.length < 2) {
                Utils.sendMessage(sender, "&c/hdb search <name>");
                return true;
            }
            if (!(sender instanceof Player)) {
                Utils.sendMessage(sender, "&cOnly players may open the database.");
                return true;
            }
            Player player = (Player) sender;

            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                builder.append(args[i]);
                if (i != args.length - 1) {
                    builder.append(" ");
                }
            }
            String name = builder.toString();
            Utils.sendMessage(sender, "Searching for &e" + name);
            InventoryUtils.openSearchDatabase(player, name);
            return true;
        }

        if (sub.equalsIgnoreCase("tagsearch") || sub.equalsIgnoreCase("ts")) {
            if (!sender.hasPermission("headdb.tagsearch")) {
                Utils.sendMessage(sender, "&cNo permission!");
                return true;
            }
            if (args.length < 2) {
                Utils.sendMessage(sender, "&c/hdb tagsearch <tag>");
                return true;
            }
            if (!(sender instanceof Player)) {
                Utils.sendMessage(sender, "&cOnly players may open the database.");
                return true;
            }
            Player player = (Player) sender;

            String tag = args[1];
            Utils.sendMessage(sender, "Searching for heads with tag &e" + tag);
            InventoryUtils.openTagSearchDatabase(player, tag);
            return true;
        }

        if (sub.equalsIgnoreCase("give") || sub.equalsIgnoreCase("g")) {
            if (!sender.hasPermission("headdb.give")) {
                Utils.sendMessage(sender, "&cNo permission!");
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
                    Utils.sendMessage(sender, "&cPlayer is not online!");
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
                Utils.sendMessage(sender, "Given &c" + target.getName() + " &ex" + amount + " " + head.getName());
                return true;
            } catch (NumberFormatException nfe) {
                Utils.sendMessage(sender, "&cID/Amount must be a number!");
                return true;
            }
        }

        Utils.sendMessage(sender, " ");
        Utils.sendMessage(sender, "&c&lHeadDB &c- &5Commands");
        Utils.sendMessage(sender, "&7&oParameters:&c command &9(aliases)&c arguments... &7- Description");
        Utils.sendMessage(sender, " > &c/hdb &7- Opens the database");
        Utils.sendMessage(sender, " > &c/hdb info &9(i) &7- Plugin Information");
        Utils.sendMessage(sender, " > &c/hdb search &9(s) &c<name> &7- Search for heads matching a name");
        Utils.sendMessage(sender, " > &c/hdb tagsearch &9(ts) &c<tag> &7- Search for heads matching a tag");
        Utils.sendMessage(sender, " > &c/hdb give &9(g) &c<id> <player> &6[amount] &7- Give player a head");
        Utils.sendMessage(sender, " ");
        return true;
    }

}
