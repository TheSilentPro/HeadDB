package tsp.headdb.core.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tsp.headdb.HeadDB;
import tsp.headdb.core.api.HeadAPI;
import tsp.headdb.core.util.Utils;
import tsp.headdb.implementation.category.Category;
import tsp.headdb.implementation.head.Head;
import tsp.headdb.implementation.head.LocalHead;
import tsp.smartplugin.inventory.Button;
import tsp.smartplugin.inventory.PagedPane;
import tsp.smartplugin.inventory.Pane;
import net.wesjd.anvilgui.AnvilGUI;
import tsp.smartplugin.utils.StringUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandMain extends HeadDBCommand implements CommandExecutor, TabCompleter {

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
                getLocalization().sendConsoleMessage("noConsole");
                return;
            }

            if (!player.hasPermission(getPermission())) {
                getLocalization().sendMessage(sender, "noPermission");
                return;
            }
            getLocalization().sendMessage(player.getUniqueId(), "openDatabase");

            Pane pane = new Pane(6, Utils.translateTitle(getLocalization().getMessage(player.getUniqueId(), "menu.main.title").orElse("&cHeadDB &7(" + HeadAPI.getTotalHeads() + ")"), HeadAPI.getTotalHeads(), "Main"));
            // Set category buttons
            for (Category category : Category.VALUES) {
                pane.setButton(getInstance().getConfig().getInt("gui.main.category." + category.getName(), category.getDefaultSlot()), new Button(category.getItem(player.getUniqueId()), e -> {
                    e.setCancelled(true);
                    if (e.isLeftClick()) {
                        Bukkit.dispatchCommand(e.getWhoClicked(), "hdb open " + category.getName());
                    } else if (e.isRightClick()) {
                        new AnvilGUI.Builder()
                                .onComplete((p, text) -> {
                                    try {
                                        int page = Integer.parseInt(text);
                                        // to be replaced with own version of anvilgui
                                        List<Head> heads = HeadAPI.getHeads(category);
                                        PagedPane main = Utils.createPaged(player, Utils.translateTitle(getLocalization().getMessage(player.getUniqueId(), "menu.category.name").orElse(category.getName()), heads.size(), category.getName()));
                                        Utils.addHeads(player, category, main, heads);
                                        main.selectPage(page);
                                        main.reRender();
                                        return AnvilGUI.Response.openInventory(main.getInventory());
                                    } catch (NumberFormatException nfe) {
                                        return AnvilGUI.Response.text("Invalid number!");
                                    }
                                })
                                .text("Query")
                                .title(StringUtils.colorize(getLocalization().getMessage(player.getUniqueId(), "menu.main.category.page.name").orElse("Enter page")))
                                .plugin(getInstance())
                                .open(player);
                    }
                }));
            }

            // Set meta buttons
            pane.setButton(getInstance().getConfig().getInt("gui.main.meta.favorites.slot"), new Button(Utils.getItemFromConfig("gui.main.meta.favorites.item", Material.BOOK), e -> {
                e.setCancelled(true);
                List<Head> heads = HeadAPI.getFavoriteHeads(player.getUniqueId());
                PagedPane main = Utils.createPaged(player, Utils.translateTitle(getLocalization().getMessage(player.getUniqueId(), "menu.main.favorites").orElse("Favorites"), heads.size(), "Favorites"));
                for (Head head : heads) {
                    main.addButton(new Button(head.getItem(player.getUniqueId()), fe -> {
                        if (fe.isLeftClick()) {
                            ItemStack favoriteItem = head.getItem(player.getUniqueId());
                            if (fe.isShiftClick()) {
                                favoriteItem.setAmount(64);
                            }

                            player.getInventory().addItem(favoriteItem);
                        }
                    }));
                }
            }));

            pane.setButton(getInstance().getConfig().getInt("gui.main.meta.search.slot"), new Button(Utils.getItemFromConfig("gui.main.meta.search.item", Material.DARK_OAK_SIGN), e -> {
                e.setCancelled(true);
                new AnvilGUI.Builder()
                        .onComplete((p, query) -> {
                            // Copied from CommandSearch
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
                                    heads.addAll(headList.stream().filter(head -> Utils.matches(head.getName(), query)).toList());
                                }
                            } else {
                                // query is <=3, no point in looking for prefixes
                                heads.addAll(headList.stream().filter(head -> Utils.matches(head.getName(), query)).toList());
                            }

                            PagedPane main = Utils.createPaged(player, Utils.translateTitle(getLocalization().getMessage(player.getUniqueId(), "menu.search.name").orElse("&cHeadDB - &eSearch Results"), heads.size(), "None", query));
                            Utils.addHeads(player, null, main, heads);
                            main.reRender();
                            return AnvilGUI.Response.openInventory(main.getInventory());
                        })
                        .title(StringUtils.colorize(getLocalization().getMessage(player.getUniqueId(), "menu.main.search.name").orElse("Search")))
                        .text("Query")
                        .plugin(getInstance())
                        .open(player);
            }));

            pane.setButton(getInstance().getConfig().getInt("gui.main.meta.local.slot"), new Button(Utils.getItemFromConfig("gui.main.meta.local.item", Material.COMPASS), e -> {
                Set<LocalHead> localHeads = HeadAPI.getLocalHeads();
                PagedPane localPane = Utils.createPaged(player, Utils.translateTitle(getLocalization().getMessage(player.getUniqueId(), "menu.main.local.name").orElse("Local Heads"), localHeads.size(), "Local"));
                for (LocalHead head : localHeads) {
                    localPane.addButton(new Button(head.getItem(), le -> {
                        if (le.isLeftClick()) {
                            ItemStack localItem = head.getItem();
                            if (le.isShiftClick()) {
                                localItem.setAmount(64);
                            }

                            player.getInventory().addItem(localItem);
                        }
                    }));
                }

                localPane.open(player);
            }));

            // Fill
            Utils.fill(pane, Utils.getItemFromConfig("gui.main.fill", Material.BLACK_STAINED_GLASS));

            pane.open(player);
            return;
        }

        getInstance().getCommandManager().getCommand(args[0]).ifPresentOrElse(command -> {
            if (sender instanceof Player player && !player.hasPermission(command.getPermission())) {
                getLocalization().sendMessage(player.getUniqueId(), "noPermission");
                return;
            }

            command.handle(sender, args);
        }, () -> getLocalization().sendMessage(sender, "invalidSubCommand"));
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        handle(sender, args);
        return true;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return new ArrayList<>(getCompletions());
        } else {
            Optional<SubCommand> sub = getInstance().getCommandManager().getCommand(args[0]);
            if (sub.isPresent()) {
                return new ArrayList<>(sub.get().getCompletions());
            }
        }

        return Collections.singletonList("error"); // for debug purpose, todo: remove
    }

}
