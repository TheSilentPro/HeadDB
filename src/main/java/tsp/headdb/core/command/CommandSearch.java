package tsp.headdb.core.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tsp.headdb.core.api.HeadAPI;
import tsp.headdb.core.util.Utils;
import tsp.headdb.implementation.head.Head;
import tsp.smartplugin.inventory.PagedPane;

import java.util.ArrayList;
import java.util.List;

public class CommandSearch extends SubCommand {

    public CommandSearch() {
        super("search", "s");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            getLocalization().sendConsoleMessage("noConsole");
            return;
        }

        if  (args.length < 2) {
            getLocalization().sendMessage(player, "invalidArguments");
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]);
            if (i != args.length - 1) {
                builder.append(" ");
            }
        }

        final String query = builder.toString();

        List<Head> heads = new ArrayList<>();
        List<Head> headList = HeadAPI.getHeads();
        if (query.length() > 3) {
            if (query.startsWith("id:")) {
                try {
                    HeadAPI.getHeadById(Integer.parseInt(query.substring(3))).ifPresent(heads::add);
                } catch (NumberFormatException ignored) {
                }
            } else if (query.startsWith("tg:")) {
                heads.addAll(headList.stream().filter(head -> Utils.matches(head.getTags(), query.substring(3))).toList());
            } else {
                // no query prefix
                heads.addAll(headList.stream().filter(head -> Utils.matches(head.getName(), query.substring(3))).toList());
            }
        } else {
            // query is <=3, no point in looking for prefixes
            heads.addAll(headList.stream().filter(head -> Utils.matches(head.getName(), query.substring(3))).toList());
        }

        getLocalization().sendMessage(player.getUniqueId(), "searchCommand", msg -> msg.replace("%query%", query));
        PagedPane main = Utils.createPaged(player, Utils.translateTitle(getLocalization().getMessage(player.getUniqueId(), "menu.search.name").orElse("&cHeadDB - &eSearch Results"), heads.size(), "None", query));
        Utils.addHeads(player, null, main, heads);
        getLocalization().sendMessage(player.getUniqueId(), "searchCommandResults", msg -> msg.replace("%size%", String.valueOf(heads.size())).replace("%query%", query));
        main.open(player);
    }

}