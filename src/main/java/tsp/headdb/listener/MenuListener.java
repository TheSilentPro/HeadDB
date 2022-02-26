package tsp.headdb.listener;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import tsp.headdb.HeadDB;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.implementation.Category;
import tsp.headdb.inventory.InventoryUtils;
import tsp.headdb.util.Utils;

/**
 * This handles all clicks on the main inventory
 */
public class MenuListener implements Listener {

    public MenuListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getWhoClicked();
        if (e.getView().getTitle().startsWith(Utils.colorize("&c&lHeadDB"))) {
            e.setCancelled(true);
            Inventory inventory = e.getClickedInventory();

            if (inventory != null) {
                int slot = e.getSlot();
                ItemStack item = inventory.getItem(slot);

                if (item != null && item.getType() != Material.AIR) {
                    String name = ChatColor.stripColor(item.getItemMeta().getDisplayName().toLowerCase());
                    if (name.equalsIgnoreCase("favorites")) {
                        if (!player.hasPermission("headdb.favorites")) {
                            player.closeInventory();
                            Utils.sendMessage(player, "&cYou do not have permission for favorites!");
                            Utils.playSound(player, "noPermission");
                            return;
                        }
                        InventoryUtils.openFavoritesMenu(player);
                        Utils.playSound(player, "open");
                        return;
                    }
                    if (name.equalsIgnoreCase("local")) {
                        if (!player.hasPermission("headdb.local")) {
                            player.closeInventory();
                            Utils.sendMessage(player, "&cYou do not have permission to view local heads!");
                            Utils.playSound(player, "noPermission");
                            return;
                        }
                        InventoryUtils.openLocalMenu(player);
                        Utils.playSound(player, "open");
                        return;
                    }
                    if (name.equalsIgnoreCase("search")) {
                        if (!player.hasPermission("headdb.search")) {
                            player.closeInventory();
                            Utils.sendMessage(player, "&cYou do not have permission for the search function!");
                            Utils.playSound(player, "noPermission");
                            return;
                        }

                        new AnvilGUI.Builder()
                                .onComplete((p, text) -> {
                                    InventoryUtils.openSearchDatabase(p, text);
                                    return AnvilGUI.Response.openInventory(InventoryUtils.openSearchDatabase(p, text).getInventory());
                                })
                                .title("Search Heads")
                                .text("Name...")
                                .plugin(HeadDB.getInstance())
                                .open(player);
                        Utils.playSound(player, "open");
                        return;
                    }

                    Category category = Category.getByName(name);

                    if (category != null) {
                        if (HeadDB.getInstance().getConfig().getBoolean("requireCategoryPermission") && !player.hasPermission("headdb.category." + category)) {
                            Utils.sendMessage(player, "&cYou do not have permission for this category! (&e" + category.getName() + "&c)");
                            Utils.playSound(player, "noPermission");
                            return;
                        }

                        Utils.playSound(player, "open");
                        HeadAPI.openCategoryDatabase(player, category);
                    }
                }
            }
        }
    }

}
