package tsp.headdb.core.command;

import org.bukkit.command.CommandSender;
import tsp.smartplugin.player.PlayerUtils;

public class CommandHelp extends SubCommand {

    public CommandHelp() {
        super("help");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        PlayerUtils.sendMessage(sender, "&7<==================== [ &cHeadDB &7| &5Commands ] &7====================>");
        PlayerUtils.sendMessage(sender, "&7Format: /hdb &9<sub-command>(aliases) &c<parameters> &7- Description");
        PlayerUtils.sendMessage(sender, "&7Required: &c<> &7| Optional: &b[]");
        PlayerUtils.sendMessage(sender, " ");
        PlayerUtils.sendMessage(sender, "&7/hdb &9open(category|c) &c<category> &b[page] &7- Open a specific category.");
        PlayerUtils.sendMessage(sender, "&7<===============================================================>");
    }

}
