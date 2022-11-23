package tsp.headdb.core.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import tsp.headdb.HeadDB;
import tsp.headdb.core.api.HeadAPI;

public class CommandUpdate extends SubCommand {

    public CommandUpdate() {
        super("update", "u");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        getLocalization().sendMessage(sender, "updateDatabase");
        HeadAPI.getDatabase().update((time, result) -> {
            HeadDB.getInstance().getLog().debug("Database Updated! Heads: " + result.values().size() + " | Took: " + time + "ms");
            Bukkit.getScheduler().runTask(HeadDB.getInstance(), () -> getLocalization().sendMessage(sender, "updateDatabaseDone", msg -> msg.replace("%size%", String.valueOf(result.values().size()))));
        });
    }

}
