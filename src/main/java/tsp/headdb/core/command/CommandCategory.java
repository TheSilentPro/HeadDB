package tsp.headdb.core.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import tsp.headdb.HeadDB;
import tsp.headdb.core.api.HeadAPI;
import tsp.headdb.core.util.Utils;
import tsp.headdb.implementation.category.Category;
import tsp.headdb.implementation.head.Head;
import tsp.smartplugin.inventory.Button;
import tsp.smartplugin.inventory.paged.PagedPane;

import java.util.List;
import java.util.stream.Collectors;

public class CommandCategory extends SubCommand {

    public CommandCategory() {
        super("open", new String[]{"category", "c"});
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            getLocalization().sendConsoleMessage("noConsole");
            return;
        }

        Category.getByName(args[1]).ifPresentOrElse(category -> {
            boolean requirePermission = HeadDB.getInstance().getConfig().getBoolean("requireCategoryPermission");
            if (requirePermission && !player.hasPermission("headdb.category." + category.getName())) {
                getLocalization().sendMessage(player.getUniqueId(), "noPermission");
                return;
            }

            int page = 1;
            if (args[2] != null) {
                try {
                    page = Integer.parseInt(args[2]);
                } catch (NumberFormatException ignored) {}
            }

            List<Head> heads = HeadAPI.getHeads(category).collect(Collectors.toList());
            PagedPane main = new PagedPane(6, 6, Utils.translateTitle(getLocalization().getMessage(player.getUniqueId(), "menu.category.name").orElse(category.getName()), heads.size(), category.getName()));
            Utils.addHeads(player, category, main, heads);
            if (page > main.getPageAmount()) {
                getLocalization().sendMessage(player.getUniqueId(), "invalidPageIndex", msg -> msg.replace("%pages%", String.valueOf(main.getPageAmount())));
                return;
            }

            main.selectPage(page);
            main.open(player);
        }, () -> getLocalization().sendMessage(player.getUniqueId(), "invalidCategory"));
    }

}
