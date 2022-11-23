package tsp.headdb.core.command;

import org.bukkit.command.CommandSender;
import tsp.smartplugin.player.PlayerUtils;

public class CommandHelp extends SubCommand {

    public CommandHelp() {
        super("help", "h");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        PlayerUtils.sendMessage(sender, "&7<==================== [ &cHeadDB &7| &5Commands ] &7====================>");
        PlayerUtils.sendMessage(sender, "&7Format: /hdb &9<sub-command>(aliases) &c<parameters> &7- Description");
        PlayerUtils.sendMessage(sender, "&7Required: &c<> &7| Optional: &b[]");
        PlayerUtils.sendMessage(sender, " ");
        PlayerUtils.sendMessage(sender, "&7/hdb &9info(i) &7- Show plugin information.");
        PlayerUtils.sendMessage(sender, "&7/hdb &9open(o) &c<category> &b[page] &7- Open a specific category.");
        PlayerUtils.sendMessage(sender, "&7/hdb &9search(s) &b(id:|tg:)&c<query> &7- Search for specific heads.");
        PlayerUtils.sendMessage(sender, "&7/hdb &9give(g) &b(t:)&c<id> <player> &b[amount] &7- Give the player a specific head.");
        PlayerUtils.sendMessage(sender, "&7/hdb &9update(u) &7- Manually update the database.");
        PlayerUtils.sendMessage(sender, "&7/hdb &9reload(r) &7- Reload configuration files.");
        PlayerUtils.sendMessage(sender, "&7/hdb &9language(l) &7- Change your language.");
        PlayerUtils.sendMessage(sender, "&7/hdb &9settings(st) &7- Open the settings menu.");
        PlayerUtils.sendMessage(sender, "&7/hdb &9texture(t) &7- Get the texture for the head your item.");
        PlayerUtils.sendMessage(sender, "&7<===============================================================>");
    }

}
