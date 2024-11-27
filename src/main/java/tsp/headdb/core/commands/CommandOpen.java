package tsp.headdb.core.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tsp.headdb.HeadDB;
import tsp.headdb.api.model.Category;
import tsp.headdb.core.util.MenuSetup;

import java.util.Arrays;
import java.util.List;

/**
 * @author TheSilentPro (Silent)
 */
public class CommandOpen extends HDBCommand {

    private final static List<String> categories = Arrays.stream(Category.VALUES).map(Category::getName).toList();

    public CommandOpen() {
        super("open", "[category]", "Open the head menu. Optionally a specific category.", true, "o");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                localization.sendMessage(sender, "noConsole");
                return;
            }

            MenuSetup.mainGui.open(player);
            localization.sendMessage(player, "openDatabase");
            return;
        }

        Category.getByName(args[0]).ifPresentOrElse(category -> MenuSetup.categoryGuis.get(category).open((Player) sender),
                () -> localization.sendMessage(sender, "invalidCategory", msg -> msg.replace("%name%", args[0]))
        );
    }

    @Override
    public List<String> handleCompletions(CommandSender sender, String[] args) {
        return categories;
    }

    @Override
    public boolean waitUntilReady() {
        return true;
    }

}