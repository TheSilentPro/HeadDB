package tsp.headdb.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.implementation.Head;
import tsp.headdb.util.Utils;

public class GiveCommand implements HeadSubCommand {

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!sender.hasPermission("headdb.give")) {
            Utils.sendMessage(sender, getLocalization().getMessage("noPermission"));
            return;
        }

        if (args.length < 3) {
            Utils.sendMessage(sender, "&c/hdb give <id> <player> &6[amount]");
            return;
        }

        try {
            int id = Integer.parseInt(args[1]);
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                Utils.sendMessage(sender, getLocalization().getMessage("invalidPlayer"));
                return;
            }

            int amount = 1;
            if (args.length > 3) {
                amount = Integer.parseInt(args[3]);
            }

            Head head = HeadAPI.getHeadByID(id);
            if (head == null) {
                Utils.sendMessage(sender, "&cCould not find head with id &e" + id);
                return;
            }

            ItemStack item = head.getMenuItem();
            item.setAmount(amount);
            target.getInventory().addItem(item);
            Utils.sendMessage(sender, "&7Gave &6" + target.getName() + " &ex" + amount + " " + head.getName());
        } catch (NumberFormatException nfe) {
            Utils.sendMessage(sender, "&cID/Amount must be a number!");
        }
    }
    
}
