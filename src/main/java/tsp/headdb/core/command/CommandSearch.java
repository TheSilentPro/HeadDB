package tsp.headdb.core.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tsp.headdb.core.api.HeadAPI;
import tsp.headdb.core.util.Utils;
import tsp.headdb.implementation.head.Head;
import tsp.smartplugin.inventory.paged.PagedPane;

import java.util.List;
import java.util.stream.Collectors;

public class CommandSearch extends SubCommand {

    public CommandSearch() {
        super("search", new String[]{"s"});
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            getLocalization().sendConsoleMessage("noConsole");
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }

        String query = builder.toString();
        getLocalization().sendMessage(player.getUniqueId(), "searchCommand", msg -> msg.replace("%query%", query));

        List<Head> heads = HeadAPI.getHeads().filter(head -> Utils.matches(head.getName(), query)).collect(Collectors.toList());
        PagedPane main = new PagedPane(6, 6, Utils.translateTitle(getLocalization().getMessage(player.getUniqueId(), "menu.search.name").orElse("&cHeadDB - &eSearch Results"), heads.size(), "None", query));
        Utils.addHeads(player, null, main, heads);
        getLocalization().sendMessage(player.getUniqueId(), "searchCommandResults", msg -> msg.replace("%size%", String.valueOf(heads.size())).replace("%query%", query));
    }

}
