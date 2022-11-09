package tsp.headdb.core.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tsp.headdb.HeadDB;

import java.util.Set;

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
            getLocalization().sendMessage(sender, "invalidLanguage", msg -> msg.replace("%languages%", toString(getLocalization().getData().keySet())));
            return;
        }

        if (!(sender instanceof Player player)) {
            getLocalization().setConsoleLanguage(lang);
        } else {
            getLocalization().setLanguage(player.getUniqueId(), lang);
        }

        getLocalization().sendMessage(sender, "languageChanged", msg -> msg.replace("%language%", lang));
    }

    private String toString(Set<String> set) {
        String[] array = set.toArray(new String[0]);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(",");
            }
        }

        return builder.toString();
    }

}
