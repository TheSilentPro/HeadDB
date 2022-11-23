package tsp.headdb.core.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tsp.headdb.HeadDB;
import tsp.headdb.core.util.Utils;

public class CommandLanguage extends SubCommand {

    public CommandLanguage() {
        super("language", HeadDB.getInstance().getLocalization().getData().keySet(), "l", "lang");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length < 2) {
            getLocalization().sendMessage(sender, "invalidArguments");
            return;
        }
        String lang = args[1];

        if (!getLocalization().getData().containsKey(lang)) {
            getLocalization().sendMessage(sender, "invalidLanguage", msg -> msg.replace("%languages%", Utils.toString(getLocalization().getData().keySet())));
            return;
        }

        if (!(sender instanceof Player player)) {
            getLocalization().setConsoleLanguage(lang);
        } else {
            getLocalization().setLanguage(player.getUniqueId(), lang);
        }

        getLocalization().sendMessage(sender, "languageChanged", msg -> msg.replace("%language%", lang));
    }

}
