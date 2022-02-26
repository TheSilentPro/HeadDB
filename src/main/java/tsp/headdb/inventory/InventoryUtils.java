package tsp.headdb.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import tsp.headdb.HeadDB;
import tsp.headdb.implementation.Head;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.implementation.LocalHead;
import tsp.headdb.implementation.Category;
import tsp.headdb.economy.BasicEconomyProvider;
import tsp.headdb.api.event.PlayerHeadPurchaseEvent;
import tsp.headdb.util.Localization;
import tsp.headdb.util.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Class for handling the "dirty" work
 * such as inventories and economy.
 */
public class InventoryUtils {

    private static final Localization localization = HeadDB.getInstance().getLocalization();
    private static final Map<String, Integer> uiLocation = new HashMap<>();
    private static final Map<String, ItemStack> uiItem = new HashMap<>();

    public static void openLocalMenu(Player player) {
        List<LocalHead> heads = HeadAPI.getLocalHeads();

        PagedPane pane = new PagedPane(4, 6,
                replace(localization.getMessage("menu.local"), heads.size(), "Local", "None", player));
        for (LocalHead localHead : heads) {
            pane.addButton(new Button(localHead.getMenuItem(), e -> {
                if (e.getClick() == ClickType.SHIFT_LEFT) {
                    purchaseHead(player, localHead, 64, "local", localHead.getName());
                    return;
                }
                if (e.getClick() == ClickType.LEFT) {
                    purchaseHead(player, localHead, 1, "local", localHead.getName());
                    return;
                }
                if (e.getClick() == ClickType.RIGHT) {
                    Utils.sendMessage(player, localization.getMessage("localFavorites"));
                }
            }));
        }

        pane.open(player);
    }

    public static void openFavoritesMenu(Player player) {
        List<Head> heads = HeadAPI.getFavoriteHeads(player.getUniqueId());

        PagedPane pane = new PagedPane(4, 6,
                replace(localization.getMessage("menu.favorites"), heads.size(), "Favorites", "None", player));
        for (Head head : heads) {
            pane.addButton(new Button(head.getMenuItem(), e -> {
                if (e.getClick() == ClickType.SHIFT_LEFT) {
                    purchaseHead(player, head, 64, head.getCategory().getName(), head.getName());
                }
                if (e.getClick() == ClickType.LEFT) {
                    purchaseHead(player, head, 1, head.getCategory().getName(), head.getName());
                }
                if (e.getClick() == ClickType.RIGHT) {
                    HeadAPI.removeFavoriteHead(player.getUniqueId(), head.getValue());
                    openFavoritesMenu(player);
                    Utils.sendMessage(player, "&7Removed &e" + head.getName() + " &7from favorites.");
                    Utils.playSound(player, "removeFavorite");
                }
            }));
        }

        pane.open(player);
    }

    public static PagedPane openSearchDatabase(Player player, String search) {
        List<Head> heads = HeadAPI.getHeadsByName(search);

        PagedPane pane = new PagedPane(4, 6,
                replace(localization.getMessage("menu.search"), heads.size(), "None", search, player));
        for (Head head : heads) {
            pane.addButton(new Button(head.getMenuItem(), e -> {
                if (e.getClick() == ClickType.SHIFT_LEFT) {
                    purchaseHead(player, head, 64, head.getCategory().getName(), head.getName());
                }
                if (e.getClick() == ClickType.LEFT) {
                    purchaseHead(player, head, 1, head.getCategory().getName(), head.getName());
                }
                if (e.getClick() == ClickType.RIGHT) {
                    if (!player.hasPermission("headdb.favorites")) {
                        Utils.sendMessage(player, localization.getMessage("noPermission"));
                        Utils.playSound(player, "noPermission");
                        return;
                    }

                    HeadAPI.addFavoriteHead(player.getUniqueId(), head.getValue());
                    Utils.sendMessage(player, "&7Added &e" + head.getName() + " &7to favorites.");
                    Utils.playSound(player, "addFavorite");
                }
            }));
        }

        pane.open(player);
        return pane;
    }

    public static void openTagSearchDatabase(Player player, String tag) {
        List<Head> heads = HeadAPI.getHeadsByTag(tag);

        PagedPane pane = new PagedPane(4, 6,
                replace(localization.getMessage("menu.tagSearch"), heads.size(), "None", tag, player));
        for (Head head : heads) {
            pane.addButton(new Button(head.getMenuItem(), e -> {
                if (e.getClick() == ClickType.SHIFT_LEFT) {
                    purchaseHead(player, head, 64, head.getCategory().getName(), head.getName());
                }
                if (e.getClick() == ClickType.LEFT) {
                    purchaseHead(player, head, 1, head.getCategory().getName(), head.getName());
                }
                if (e.getClick() == ClickType.RIGHT) {
                    if (!player.hasPermission("headdb.favorites")) {
                        Utils.sendMessage(player, localization.getMessage("noPermission"));
                        Utils.playSound(player, "noPermission");
                        return;
                    }

                    HeadAPI.addFavoriteHead(player.getUniqueId(), head.getValue());
                    Utils.sendMessage(player, "&7Added &e" + head.getName() + " &7to favorites.");
                    Utils.playSound(player, "addFavorite");
                }
            }));
        }

        pane.open(player);
    }

    public static void openCategoryDatabase(Player player, Category category) {
        List<Head> heads = HeadAPI.getHeads(category);

        PagedPane pane = new PagedPane(4, 6,
                replace(localization.getMessage("menu.category"), heads.size(), category.getName(), "None", player));
        for (Head head : heads) {
            pane.addButton(new Button(head.getMenuItem(), e -> {
                if (e.getClick() == ClickType.SHIFT_LEFT) {
                    purchaseHead(player, head, 64, head.getCategory().getName(), head.getName());
                }
                if (e.getClick() == ClickType.LEFT) {
                    purchaseHead(player, head, 1, head.getCategory().getName(), head.getName());
                }
                if (e.getClick() == ClickType.RIGHT) {
                    if (!player.hasPermission("headdb.favorites")) {
                        Utils.sendMessage(player, localization.getMessage("noPermission"));
                        Utils.playSound(player, "noPermission");
                        return;
                    }

                    HeadAPI.addFavoriteHead(player.getUniqueId(), head.getValue());
                    Utils.sendMessage(player, "&7Added &e" + head.getName() + " &7to favorites.");
                    Utils.playSound(player, "addFavorite");
                }
            }));
        }

        pane.open(player);
    }

    public static void openDatabase(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54,
                replace(localization.getMessage("menu.main"), HeadAPI.getHeads().size(), "Main", "None", player));

        for (Category category : Category.cache) {
            ItemStack item = getUIItem(category.getName(), category.getItem());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Utils.colorize(localization.getMessage("menu.heads." + category.getName())));
            List<String> lore = new ArrayList<>();
            lore.add(Utils.colorize(replace(localization.getMessage("menu.lore"), HeadAPI.getHeads(category).size(), "Main", "None", player)));
            meta.setLore(lore);
            item.setItemMeta(meta);
            inventory.setItem(getUILocation(category.getName(), category.getLocation()), item);
        }

        if (player.hasPermission("headdb.favorites")) {
            inventory.setItem(getUILocation("favorites", 39), buildButton(
                getUIItem("favorites", new ItemStack(Material.BOOK)),
                "&eFavorites",
                "",
                "&8Click to view your favorites")
            );
        }

        if (player.hasPermission("headdb.search")) {
            inventory.setItem(getUILocation("search", 40), buildButton(
                getUIItem("search", new ItemStack(Material.DARK_OAK_SIGN)),
                "&9Search",
                "",
                "&8Click to open search menu"
            ));
        }

        if (player.hasPermission("headdb.local")) {
            inventory.setItem(getUILocation("local", 41), buildButton(
                getUIItem("local", new ItemStack(Material.COMPASS)),
                "&aLocal",
                "",
                "&8Heads from any players that have logged on the server"
            ));
        }

        fill(inventory);
        player.openInventory(inventory);
    }

    private static void fill(Inventory inv) {
        ItemStack item = getUIItem("fill", new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        // Do not bother filling the inventory if item to fill it with is AIR.
        if (item == null || item.getType() == Material.AIR) return;

        // Fill any non-empty inventory slots with the given item.
        int size = inv.getSize();
        for (int i = 0; i < size; i++) {
            ItemStack slotItem = inv.getItem(i);
            if (slotItem == null || slotItem.getType() == Material.AIR) {
                inv.setItem(i, item);
            }
        }
    }

    private static int getUILocation(String category, int slot) {
        // Try to use the cached value first.
        if (uiLocation.containsKey(category)) return uiLocation.get(category);

        // Try to get the value from the config file.
        if (HeadDB.getInstance().getConfig().contains("ui.category." + category + ".location")) {
            uiLocation.put(category, HeadDB.getInstance().getConfig().getInt("ui.category." + category + ".location"));
            return uiLocation.get(category);
        }

        // No valid value in the config file, return the given default.
        uiLocation.put(category, slot);
        return slot;
    }

    private static ItemStack getUIItem(String category, ItemStack item) {
        // Try to use the cached item first.
        if (uiItem.containsKey(category)) return uiItem.get(category);

        // Try to get a head from the config file.
        if (HeadDB.getInstance().getConfig().contains("ui.category." + category + ".head")) {
            int id = HeadDB.getInstance().getConfig().getInt("ui.category." + category + ".head");
            Head head = HeadAPI.getHeadByID(id);
            if (head != null) {
                uiItem.put(category, head.getMenuItem());
                return uiItem.get(category);
            }
        }

        // Try to get an item from the config file.
        if (HeadDB.getInstance().getConfig().contains("ui.category." + category + ".item")) {
            String cfg = HeadDB.getInstance().getConfig().getString("ui.category." + category + ".item");
            Material mat = Material.matchMaterial(cfg);

            // AIR is allowed as the fill material for the menu, but not as a category item.
            if (mat != null && (category.equals("fill") || mat != Material.AIR)) {
                uiItem.put(category, new ItemStack(mat));
                return uiItem.get(category);
            }
        }

        // No valid head or item in the config file, return the given default.
        uiItem.put(category, item);
        return item;
    }


    private static ItemStack buildButton(ItemStack item, String name, String... lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Utils.colorize(name));
        List<String> list = meta.getLore();
        if (list == null) {
            list = new ArrayList<>();
        }

        for (String line : lore) {
            list.add(Utils.colorize(line));
        }

        item.setItemMeta(meta);
        return item;
    }

    public static double getCategoryCost(Player player, String category) {
        // If the player has the permission headdb.economy.free or headdb.economy.CATEGORY.free, the item is free.
        if (player.hasPermission("headdb.economy.free") || player.hasPermission("headdb.economy." + category + ".free")) return 0;

        // Otherwise, get the price for this category from the config file.
        return HeadDB.getInstance().getConfig().getDouble("economy.cost." + category);
    }

    public static void processPayment(Player player, int amount, String category, String description, Consumer<Boolean> result) {
        Utils.async(task -> {
            BasicEconomyProvider economyProvider = HeadDB.getInstance().getEconomyProvider();

            // If economy is disabled or no plugin is present, the item is free.
            // Don't mention receiving it for free in this case, since it is always free.
            if (economyProvider == null) {
                Utils.sendMessage(player, String.format(localization.getMessage("noEconomy"), amount, description));
                Utils.playSound(player, "noEconomy");
                result.accept(true);
                return;
            }

            BigDecimal cost = BigDecimal.valueOf(getCategoryCost(player, category) * amount);

            // If the cost is higher than zero, attempt to charge for it.
            if (cost.compareTo(BigDecimal.ZERO) > 0) {
                economyProvider.canPurchase(player, cost, paymentResult -> {
                    if (paymentResult) {
                        economyProvider.charge(player, cost, chargeResult -> {
                            if (chargeResult) {
                                Utils.sendMessage(player, String.format(localization.getMessage("purchasedHead"), amount, description, cost));
                                Utils.playSound(player, "paid");
                                result.accept(true);
                                return;
                            }
                        });
                    }
                });

                Utils.sendMessage(player, String.format(localization.getMessage("notEnoughMoney"), amount, description));
                Utils.playSound(player, "unavailable");
                result.accept(false);
                return;
            }

            // Otherwise, the item is free.
            Utils.sendMessage(player, String.format(localization.getMessage("free"), amount, description));
            Utils.playSound(player, "free");
            result.accept(true);
        });
    }

    public static void purchaseHead(Player player, Head head, int amount, String category, String description) {
        Utils.sendMessage(player, String.format(localization.getMessage("processPayment"), amount, head.getName()));
        processPayment(player, amount, category, description, result -> {
            if (result) {
                PlayerHeadPurchaseEvent event = new PlayerHeadPurchaseEvent(player, head, getCategoryCost(player, category));
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    ItemStack item = head.getMenuItem();
                    item.setAmount(amount);
                    player.getInventory().addItem(item);
                }
            }
        });
    }

    private static String replace(String message, int size, String category, String search, Player player) {
        return message
                .replace("%size%", String.valueOf(size))
                .replace("%category%", category)
                .replace("%search%", search)
                .replace("%player%", player.getName());
    }

}
