package tsp.headdb.core.command;

import org.bukkit.command.CommandSender;
import tsp.headdb.HeadDB;

import java.util.HashSet;

// Todo: async
public class CommandReload extends SubCommand {

    public CommandReload() {
        super("reload", new HashSet<>(0), "r");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        getLocalization().sendMessage(sender, "reloadCommand");
        getLocalization().load();
        HeadDB.getInstance().reloadConfig();
        getLocalization().sendMessage(sender, "reloadCommandDone");
    }

}
