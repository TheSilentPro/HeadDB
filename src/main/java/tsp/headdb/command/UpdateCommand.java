package tsp.headdb.command;

import org.bukkit.command.CommandSender;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.util.Utils;

import java.util.concurrent.TimeUnit;

public class UpdateCommand implements HeadSubCommand {

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!sender.hasPermission("headdb.update")) {
            Utils.sendMessage(sender, getLocalization().getMessage("noPermission"));
            return;
        }

        Utils.sendMessage(sender, "&7Updating...");
        long start = System.currentTimeMillis();
        HeadAPI.getDatabase().update(heads -> {
            Utils.sendMessage(sender, "&7Done! Took: &a" + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start) + " &7seconds");
            Utils.sendMessage(sender, "&7There are &a" + HeadAPI.getHeads().size() + " &7heads in the database!");
        });
    }

}
