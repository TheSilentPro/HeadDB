package tsp.headdb.core.command;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import tsp.headdb.HeadDB;
import tsp.headdb.core.api.HeadAPI;
import tsp.headdb.core.util.Utils;
import tsp.headdb.implementation.category.Category;
import tsp.headdb.implementation.head.Head;
import tsp.smartplugin.inventory.Button;
import tsp.smartplugin.inventory.PagedPane;
import tsp.smartplugin.inventory.Pane;
import tsp.smartplugin.localization.TranslatableLocalization;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandMain extends HeadDBCommand implements CommandExecutor, TabCompleter {

    private final TranslatableLocalization localization = HeadDB.getInstance().getLocalization();

    public CommandMain() {
        super(
                "headdb",
                "headdb.command.open",
                HeadDB.getInstance().getCommandManager().getCommandsMap().values().stream().map(HeadDBCommand::getName).collect(Collectors.toList())
        );
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
                localization.sendMessage(sender, "noPermission");
                return;
            }
            localization.sendMessage(player.getUniqueId(), "openDatabase");

            Pane pane = new Pane(6, Utils.translateTitle(localization.getMessage(player.getUniqueId(), "menu.main.title").orElse("&cHeadDB &7(" + HeadAPI.getTotalHeads() + ")"), HeadAPI.getTotalHeads(), "Main"));
            for (Category category : Category.VALUES) {
                pane.addButton(new Button(category.getItem(player.getUniqueId()), e -> {
                    e.setCancelled(true);
                    if (e.isLeftClick()) {
                        Bukkit.dispatchCommand(e.getWhoClicked(), "hdb open " + category.getName());
                    } else if (e.isRightClick()) {
                        new AnvilGUI.Builder()
                                .onComplete((p, text) -> {
                                    try {
                                        int page = Integer.parseInt(text);
                                        // Remove when AnvilGUI adds option to return a void response
                                        List<Head> heads = HeadAPI.getHeads(category);
                                        PagedPane main = Utils.createPaged(player, Utils.translateTitle(getLocalization().getMessage(player.getUniqueId(), "menu.category.name").orElse(category.getName()), heads.size(), category.getName()));
                                        Utils.addHeads(player, category, main, heads);
                                        main.selectPage(page);
                                        main.reRender();
                                        return AnvilGUI.Response.openInventory(main.getInventory());
                                    } catch (NumberFormatException nfe) {
                                        return AnvilGUI.Response.text("Invalid number...");
                                    }
                                })
                                .text("1")
                                .title(localization.getMessage(player.getUniqueId(), "menu.main.category.page.name").orElse("Enter page"))
                                .plugin(HeadDB.getInstance())
                                .open(player);
                    }
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
        }, () -> localization.sendMessage(sender, "invalidSubCommand"));
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        handle(sender, args);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        PluginCommand provided = HeadDB.getInstance().getCommand(label);
        if (provided == null || !provided.getName().equalsIgnoreCase(getName())) {
            return new ArrayList<>(); // not this command
        }

        if (args.length == 0) {
            return new ArrayList<>(getCompletions());
        } else {
            Optional<SubCommand> sub = HeadDB.getInstance().getCommandManager().getCommand(args[1]);
            if (sub.isPresent()) {
                return new ArrayList<>(sub.get().getCompletions());
            }
        }

        return new ArrayList<>();
    }

}
