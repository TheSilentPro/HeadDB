package tsp.headdb.core.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tsp.smartplugin.localization.Message;

import java.util.Set;

public class CommandLanguage extends SubCommand {

    public CommandLanguage() {
        super("language", new String[]{"l", "lang"});
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        String lang = "en";
        if (args.length < 1) {
            getLocalization().sendMessage(new Message()
                    .receiver(sender)
                    .text("invalidArguments")
            );
            return;
        }

        if (!getLocalization().getData().containsKey(lang)) {
            getLocalization().sendMessage(new Message()
                    .receiver(sender)
                    .text("invalidLanguage")
                    .function(msg -> msg.replace("%languages%", toString(getLocalization().getData().keySet()))));
            return;
        }

        if (!(sender instanceof Player player)) {
            getLocalization().setConsoleLanguage(lang);
        } else {
            getLocalization().setLanguage(player.getUniqueId(), lang);
        }

        getLocalization().sendMessage(new Message().receiver(sender).text("languageChanged").function(msg -> msg.replace("%language%", lang)));
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
