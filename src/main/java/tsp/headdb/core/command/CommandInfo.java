package tsp.headdb.core.command;

import org.bukkit.command.CommandSender;
import tsp.headdb.HeadDB;
import tsp.headdb.core.util.Utils;
import tsp.nexuslib.player.PlayerUtils;

public class CommandInfo extends SubCommand {

    public CommandInfo() {
        super("info", "i");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (HeadDB.getInstance().getConfig().getBoolean("showAdvancedPluginInfo")) {
            PlayerUtils.sendMessage(sender, "&7Running &6HeadDB - " + Utils.getVersion().orElse(HeadDB.getInstance().getDescription().getVersion() + " &7(&4UNKNOWN SEMVER&7)"));
            //PlayerUtils.sendMessage(sender, "&7Build: " + HeadDB.getInstance().getDescription().getVersion());
            PlayerUtils.sendMessage(sender, "&7GitHub: &6https://github.com/TheSilentPro/HeadDB");
        } else {
            PlayerUtils.sendMessage(sender, "&7Running &6HeadDB &7by &6TheSilentPro (Silent)");
            PlayerUtils.sendMessage(sender, "&7GitHub: &6https://github.com/TheSilentPro/HeadDB");
        }
    }

}
