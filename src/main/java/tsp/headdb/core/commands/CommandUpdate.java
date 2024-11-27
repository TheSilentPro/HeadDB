package tsp.headdb.core.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tsp.headdb.HeadDB;
import tsp.headdb.core.util.Utils;

/**
 * @author TheSilentPro (Silent)
 */
public class CommandUpdate extends HDBCommand {

    public CommandUpdate() {
        super("update", "Forcefully start updating the database. " + ChatColor.RED + "(NOT RECOMMENDED)", false, "u");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        localization.sendMessage(sender, "updateStarted");
        HeadDB.getInstance().updateDatabase().thenAcceptAsync(heads -> localization.sendMessage(sender, "updateFinished", msg -> msg.replace("%size%", String.valueOf(heads.size()))), Utils.SYNC);
    }

    @Override
    public boolean waitUntilReady() {
        return true;
    }

}