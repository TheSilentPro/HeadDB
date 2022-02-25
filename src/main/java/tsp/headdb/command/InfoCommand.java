package tsp.headdb.command;

import org.bukkit.command.CommandSender;
import tsp.headdb.HeadDB;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.util.Utils;

public class InfoCommand implements HeadSubCommand {

    @Override
    public void handle(CommandSender sender, String[] args) {
        // These should not be customizable
        Utils.sendMessage(sender, "&7Running &cHeadDB - " + HeadDB.getInstance().getDescription().getVersion());
        Utils.sendMessage(sender, "&7Created by &c" + HeadDB.getInstance().getDescription().getAuthors());
        Utils.sendMessage(sender, "&7There are currently &c" + HeadAPI.getHeads().size() + " &7heads in the database.");
    }

}
