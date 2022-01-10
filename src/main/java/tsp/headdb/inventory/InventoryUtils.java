package tsp.headdb.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import tsp.headdb.HeadDB;
import tsp.headdb.api.Head;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.api.LocalHead;
import tsp.headdb.database.Category;
import tsp.headdb.event.PlayerHeadPurchaseEvent;
import tsp.headdb.util.Utils;

import net.milkbowl.vault.economy.Economy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for handling the "dirty" work
 * such as inventories and economy.
 */
public class InventoryUtils {

    private static final Map<String, Integer> uiLocation = new HashMap<>();
    private static final Map<String, ItemStack> uiItem = new HashMap<>();

    public static int getUILocation(String category, int slot) {
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

    public static ItemStack getUIItem(String category, ItemStack item) {
        // Try to use the cached item first.
        if (uiItem.containsKey(category)) return uiItem.get(category);

        // Try to get a head from the config file.
        if (HeadDB.getInstance().getConfig().contains("ui.category." + category + ".head")) {
            int id = HeadDB.getInstance().getConfig().getInt("ui.category." + category + ".head");
            Head head = HeadAPI.getHeadByID(id);
            if (head != null) {
                uiItem.put(category, head.getItemStack());
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

    public static void openLocalMenu(Player player) {
        PagedPane pane = new PagedPane(4, 6, Utils.colorize("&c&lHeadDB &8- &aLocal Heads"));

        List<LocalHead> heads = HeadAPI.getLocalHeads();
        for (LocalHead localHead : heads) {
            pane.addButton(new Button(localHead.getItemStack(), e -> {
                if (e.getClick() == ClickType.SHIFT_LEFT) {
                    purchaseHead(player, localHead, 64, "local", localHead.getName());
                    return;
                }
                if (e.getClick() == ClickType.LEFT) {
                    purchaseHead(player, localHead, 1, "local", localHead.getName());
                    return;
                }
                if (e.getClick() == ClickType.RIGHT) {
                    player.closeInventory();
                    Utils.sendMessage(player, "&cLocal heads can not be added to favorites!");
                }
            }));
        }

        pane.open(player);
    }

    public static void openFavoritesMenu(Player player) {
        PagedPane pane = new PagedPane(4, 6, Utils.colorize("&c&lHeadDB &8- &eFavorites: " + player.getName()));

        List<Head> heads = HeadAPI.getFavoriteHeads(player.getUniqueId());
        for (Head head : heads) {
            pane.addButton(new Button(head.getItemStack(), e -> {
                if (e.getClick() == ClickType.SHIFT_LEFT) {
                    purchaseHead(player, head, 64, head.getCategory().getName(), head.getName());
                    return;
                }
                if (e.getClick() == ClickType.LEFT) {
                    purchaseHead(player, head, 1, head.getCategory().getName(), head.getName());
                }
                if (e.getClick() == ClickType.RIGHT) {
                    HeadAPI.removeFavoriteHead(player.getUniqueId(), head.getValue());
                    openFavoritesMenu(player);
                    Utils.sendMessage(player, "&7Removed &e" + head.getName() + " &7from favorites.");
                }
            }));
        }

        pane.open(player);
    }

    public static PagedPane openSearchDatabase(Player player, String search) {
        PagedPane pane = new PagedPane(4, 6, Utils.colorize("&c&lHeadDB &8- &eSearch: " + search));

        List<Head> heads = HeadAPI.getHeadsByName(search);
        for (Head head : heads) {
            pane.addButton(new Button(head.getItemStack(), e -> {
                if (e.getClick() == ClickType.SHIFT_LEFT) {
                    purchaseHead(player, head, 64, head.getCategory().getName(), head.getName());
                    return;
                }
                if (e.getClick() == ClickType.LEFT) {
                    purchaseHead(player, head, 1, head.getCategory().getName(), head.getName());
                }
                if (e.getClick() == ClickType.RIGHT) {
                    HeadAPI.addFavoriteHead(player.getUniqueId(), head.getValue());
                    Utils.sendMessage(player, "&7Added &e" + head.getName() + " &7to favorites.");
                }
            }));
        }

        pane.open(player);
        return pane;
    }

    public static void openTagSearchDatabase(Player player, String tag) {
        PagedPane pane = new PagedPane(4, 6, Utils.colorize("&c&lHeadDB &8- &eTag Search: " + tag));

        List<Head> heads = HeadAPI.getHeadsByTag(tag);
        for (Head head : heads) {
            pane.addButton(new Button(head.getItemStack(), e -> {
                if (e.getClick() == ClickType.SHIFT_LEFT) {
                    purchaseHead(player, head, 64, head.getCategory().getName(), head.getName());
                    return;
                }
                if (e.getClick() == ClickType.LEFT) {
                    purchaseHead(player, head, 1, head.getCategory().getName(), head.getName());
                }
                if (e.getClick() == ClickType.RIGHT) {
                    HeadAPI.addFavoriteHead(player.getUniqueId(), head.getValue());
                    Utils.sendMessage(player, "&7Added &e" + head.getName() + " &7to favorites.");
                }
            }));
        }

        pane.open(player);
    }

    public static void openCategoryDatabase(Player player, Category category) {
        PagedPane pane = new PagedPane(4, 6, Utils.colorize("&c&lHeadDB &8- &e" + category.getName()));

        List<Head> heads = HeadAPI.getHeads(category);
        for (Head head : heads) {
            pane.addButton(new Button(head.getItemStack(), e -> {
                if (e.getClick() == ClickType.SHIFT_LEFT) {
                    purchaseHead(player, head, 64, head.getCategory().getName(), head.getName());
                    return;
                }
                if (e.getClick() == ClickType.LEFT) {
                    purchaseHead(player, head, 1, head.getCategory().getName(), head.getName());
                }
                if (e.getClick() == ClickType.RIGHT) {
                    HeadAPI.addFavoriteHead(player.getUniqueId(), head.getValue());
                    Utils.sendMessage(player, "&7Added &e" + head.getName() + " &7to favorites.");
                }
            }));
        }

        pane.open(player);
    }

    public static void openDatabase(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, Utils.colorize("&c&lHeadDB &8(" + HeadAPI.getHeads().size() + ")"));

        for (Category category : Category.cache) {
            ItemStack item = getUIItem(category.getName(), category.getItem());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Utils.colorize(category.getColor() + "&l" + category.getName().toUpperCase()));
            List<String> lore = new ArrayList<>();
            lore.add(Utils.colorize("&e" + HeadAPI.getHeads(category).size() + " heads"));
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

    public static void fill(Inventory inv) {
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

    public static boolean processPayment(Player player, int amount, String category, String description) {
        Economy economy = HeadDB.getInstance().getEconomy();

        // If economy is disabled or no plugin is present, the item is free.
        // Don't mention receiving it for free in this case, since it is always free.
        if (economy == null) {
            Utils.sendMessage(player, String.format("&7You received &e%d &7x &e%s&7!", amount, description));
            return true;
        }

        double cost = getCategoryCost(player, category) * amount;

        // If the cost is higher than zero, attempt to charge for it.
        if (cost > 0) {
            if (economy.has(player, cost)) {
                economy.withdrawPlayer(player, cost);
                Utils.sendMessage(player, String.format("&7You purchased &e%d &7x &e%s &7for &e%.2f %s&7!", amount, description, cost, economy.currencyNamePlural()));
                return true;
            }
            Utils.sendMessage(player, String.format("&7You do not have enough &e%s &cto purchase &e%d &cx &e%s&7.", economy.currencyNamePlural(), amount, description));
            return false;
        }

        // Otherwise, the item is free.
        Utils.sendMessage(player, String.format("&7You received &e%d &7x &e%s &7for &efree&7!", amount, description));
        return true;
    }

    public static void purchaseHead(Player player, Head head, int amount, String category, String description) {
        if (!processPayment(player, amount, category, description)) return;
        PlayerHeadPurchaseEvent event = new PlayerHeadPurchaseEvent(player, head, getCategoryCost(player, category));
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            ItemStack item = head.getItemStack();
            item.setAmount(amount);
            player.getInventory().addItem(item);
        }
    }

}
