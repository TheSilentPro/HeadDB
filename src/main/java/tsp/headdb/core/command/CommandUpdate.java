package tsp.headdb.core.command;

import org.bukkit.command.CommandSender;
import tsp.headdb.HeadDB;
import tsp.headdb.core.api.HeadAPI;
import tsp.headdb.implementation.head.HeadResult;
import tsp.helperlite.scheduler.promise.Promise;

public class CommandUpdate extends SubCommand {

    public CommandUpdate() {
        super("update", "u");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        getLocalization().sendMessage(sender, "updateDatabase");
        try (Promise<HeadResult> promise = HeadAPI.getDatabase().update()) {
            promise.thenAcceptSync(result -> {
                HeadDB.getInstance().getLog().debug("Database Updated! Heads: " + result.heads().values().size() + " | Took: " + result.elapsed() + "ms");
                getLocalization().sendMessage(sender, "updateDatabaseDone", msg -> msg.replace("%size%", String.valueOf(result.heads().values().size())));
            });
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
