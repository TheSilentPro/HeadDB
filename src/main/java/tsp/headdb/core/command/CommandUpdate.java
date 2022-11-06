package tsp.headdb.core.command;

import org.bukkit.command.CommandSender;
import tsp.headdb.core.api.HeadAPI;
import tsp.smartplugin.localization.Message;

public class CommandUpdate extends SubCommand {

    public CommandUpdate() {
        super("update", new String[]{"u"});
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        getLocalization().sendMessage(new Message()
                .receiver(sender)
                .text("updateDatabase")
        );

        HeadAPI.getDatabase().update((time, heads) -> getLocalization().sendMessage(new Message()
                .receiver(sender)
                .text("updateDatabaseDone")
                .function(msg -> msg.replace("%size%", String.valueOf(heads.size()).replace("%time%", String.valueOf(time)))))
        );
    }

}
