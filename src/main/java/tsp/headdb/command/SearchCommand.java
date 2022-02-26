package tsp.headdb.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.util.Utils;

public class SearchCommand implements HeadSubCommand {

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!sender.hasPermission("headdb.search")) {
            Utils.sendMessage(sender, getLocalization().getMessage("noPermission"));
            return;
        }
        if (!(sender instanceof Player)) {
            Utils.sendMessage(sender, getLocalization().getMessage("onlyPlayers"));
            return;
        }
        Player player = (Player) sender;

        if (args.length < 2) {
            Utils.sendMessage(player, "&c/hdb search <name>");
            return;
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
        return;
    }
    
}
