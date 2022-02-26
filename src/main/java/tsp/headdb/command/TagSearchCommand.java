package tsp.headdb.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.util.Utils;

public class TagSearchCommand implements HeadSubCommand {

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!sender.hasPermission("headdb.tagsearch")) {
            Utils.sendMessage(sender, getLocalization().getMessage("noPermission"));
            return;
        }

        if (args.length < 2) {
            Utils.sendMessage(sender, "&c/hdb tagsearch <tag>");
            return;
        }

        if (!(sender instanceof Player)) {
            Utils.sendMessage(sender, getLocalization().getMessage("onlyPlayers"));
            return;
        }

        Player player = (Player) sender;
        String tag = args[1];
        Utils.sendMessage(sender, "&7Searching for heads with tag &e" + tag);
        HeadAPI.openTagSearchDatabase(player, tag);
        return;
    }
    
}
