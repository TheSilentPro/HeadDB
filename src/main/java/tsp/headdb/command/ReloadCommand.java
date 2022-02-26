package tsp.headdb.command;

import org.bukkit.command.CommandSender;
import tsp.headdb.HeadDB;
import tsp.headdb.util.Utils;

public class ReloadCommand implements HeadSubCommand {

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!sender.hasPermission("headdb.reload")) {
            Utils.sendMessage(sender, getLocalization().getMessage("noPermission"));
            return;
        }

        HeadDB.getInstance().getLocalization().load();
        Utils.sendMessage(sender, getLocalization().getMessage("reloadMessages"));
    }

}
