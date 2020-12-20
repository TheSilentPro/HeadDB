package tsp.headdb.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tsp.headdb.api.Head;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.api.LocalHead;
import tsp.headdb.database.Category;
import tsp.headdb.util.Utils;
import tsp.headdb.util.XMaterial;

import java.util.ArrayList;
import java.util.List;

public class InventoryUtils {

    public static void openLocalMenu(Player player) {
        PagedPane pane = new PagedPane(4, 6, Utils.colorize("&c&lHeadDB &8- &aLocal Heads"));

        List<LocalHead> heads = HeadAPI.getLocalHeads();
        for (LocalHead localHead : heads) {
            pane.addButton(new Button(localHead.getItemStack(), e -> {
                if (e.getClick() == ClickType.SHIFT_LEFT) {
                    ItemStack item = localHead.getItemStack();
                    item.setAmount(64);
                    player.getInventory().addItem(item);
                    return;
                }
                if (e.getClick() == ClickType.LEFT) {
                    player.getInventory().addItem(localHead.getItemStack());
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
                    ItemStack item = head.getItemStack();
                    item.setAmount(64);
                    player.getInventory().addItem(item);
                    return;
                }
                if (e.getClick() == ClickType.LEFT) {
                    player.getInventory().addItem(head.getItemStack());
                }
                if (e.getClick() == ClickType.RIGHT) {
                    HeadAPI.removeFavoriteHead(player.getUniqueId(), head.getId());
                    openFavoritesMenu(player);
                    Utils.sendMessage(player, "Removed &e" + head.getName() + " &7from favorites.");
                }
            }));
        }

        pane.open(player);
    }

    public static void openSearchDatabase(Player player, String search) {
        PagedPane pane = new PagedPane(4, 6, Utils.colorize("&c&lHeadDB &8- &eSearch: " + search));

        List<Head> heads = HeadAPI.getHeadsByName(search);
        for (Head head : heads) {
            pane.addButton(new Button(head.getItemStack(), e -> {
                if (e.getClick() == ClickType.SHIFT_LEFT) {
                    ItemStack item = head.getItemStack();
                    item.setAmount(64);
                    player.getInventory().addItem(item);
                    return;
                }
                if (e.getClick() == ClickType.LEFT) {
                    player.getInventory().addItem(head.getItemStack());
                }
                if (e.getClick() == ClickType.RIGHT) {
                    HeadAPI.addFavoriteHead(player.getUniqueId(), head.getId());
                    Utils.sendMessage(player, "Added &e" + head.getName() + " &7to favorites.");
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
                    ItemStack item = head.getItemStack();
                    item.setAmount(64);
                    player.getInventory().addItem(item);
                    return;
                }
                if (e.getClick() == ClickType.LEFT) {
                    player.getInventory().addItem(head.getItemStack());
                }
                if (e.getClick() == ClickType.RIGHT) {
                    HeadAPI.addFavoriteHead(player.getUniqueId(), head.getId());
                    Utils.sendMessage(player, "Added &e" + head.getName() + " &8to favorites.");
                }
            }));
        }

        pane.open(player);
    }

    public static void openDatabase(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, Utils.colorize("&c&lHeadDB &8(" + HeadAPI.getHeads().size() + ")"));

        fill(inventory, XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());
        for (Category category : Category.getCategories()) {
            ItemStack item = category.getItem();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Utils.colorize(category.getColor() + "&l" + category.getName().toUpperCase()));
            List<String> lore = new ArrayList<>();
            lore.add(Utils.colorize("&e" + HeadAPI.getHeads(category).size() + " heads"));
            meta.setLore(lore);
            item.setItemMeta(meta);
            inventory.addItem(item);
        }

        inventory.setItem(39, buildButton(
                XMaterial.BOOK.parseItem(),
                "&eFavorites",
                "",
                "&8Click to view your favorites")
        );

        inventory.setItem(40, buildButton(
                XMaterial.DARK_OAK_SIGN.parseItem(),
                "&9Search",
                "",
                "&8Click to open search menu"
        ));

        inventory.setItem(41, buildButton(
                XMaterial.COMPASS.parseItem(),
                "&aLocal",
                "",
                "&8Heads from any players that have logged on the server"
        ));

        player.openInventory(inventory);
    }

    public static void fill(Inventory inv, ItemStack item) {
        int size = inv.getSize();
        int[] ignored = new int[]{20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 39, 40, 41};

        // Fill
        for (int i = 0; i < size; i++) {
            if (!contains(i, ignored)) {
                ItemStack slotItem = inv.getItem(i);
                if (slotItem == null || slotItem.getType() == Material.AIR) {
                    inv.setItem(i, item);
                }
            }
        }
    }

    private static boolean contains(int n, int... array) {
        for (int i : array) {
            if (i == n) {
                return true;
            }
        }

        return false;
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

}
