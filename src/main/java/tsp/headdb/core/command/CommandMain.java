package tsp.headdb.core.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

import tsp.headdb.HeadDB;
import tsp.headdb.core.api.HeadAPI;
import tsp.headdb.core.util.Utils;
import tsp.headdb.implementation.category.Category;
import tsp.smartplugin.inventory.Button;
import tsp.smartplugin.inventory.Pane;
import tsp.smartplugin.localization.TranslatableLocalization;
import tsp.smartplugin.utils.Validate;

import javax.annotation.ParametersAreNonnullByDefault;

public class CommandMain extends HeadDBCommand implements CommandExecutor {

    private final TranslatableLocalization localization = HeadDB.getInstance().getLocalization();

    public CommandMain() {
        super("headdb", "headdb.command.open");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void handle(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                localization.sendConsoleMessage("noConsole");
                return;
            }

            if (!player.hasPermission(getPermission())) {
                sendMessage(sender, "noPermission");
                return;
            }
            localization.sendMessage(player.getUniqueId(), "openDatabase");

            Pane pane = new Pane(6, Utils.translateTitle(localization.getMessage(player.getUniqueId(), "menu.main.title").orElse("&cHeadDB &7(" + HeadAPI.getTotalHeads() + ")"), HeadAPI.getTotalHeads(), "Main"));
            for (Category category : Category.VALUES) {
                pane.addButton(new Button(category.getItem(player.getUniqueId()), e -> {
                    if (e.isLeftClick()) {
                        Bukkit.dispatchCommand(e.getWhoClicked(), "hdb open " + category.getName());
                    } else if (e.isRightClick()) {
                        // todo: specific page
                    }

                    e.setCancelled(true);
                }));
            }

            pane.open(player);
            return;
        }

        HeadDB.getInstance().getCommandManager().getCommand(args[0]).ifPresentOrElse(command -> {
            if (sender instanceof Player player && !player.hasPermission(command.getPermission())) {
                localization.sendMessage(player.getUniqueId(), "noPermission");
                return;
            }

            command.handle(sender, args);
        }, () -> sendMessage(sender, "invalidSubCommand"));
    }

    @ParametersAreNonnullByDefault
    private void sendMessage(CommandSender sender, String key) {
        Validate.notNull(sender, "Message sender can not be null!");
        Validate.notNull(key, "Key can not be null!");

        if (sender instanceof Player player) {
            localization.sendMessage(player.getUniqueId(), key);
        } else if (sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender){
            localization.sendConsoleMessage(key);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        handle(sender, args);
        return true;
    }

}
