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
import tsp.headdb.database.Category;
import tsp.headdb.inventory.InventoryUtils;
import tsp.headdb.util.Utils;

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
                            Utils.sendMessage(player, "&cYou do not have permission for favorites!");
                            player.closeInventory();
                            return;
                        }
                        InventoryUtils.openFavoritesMenu(player);
                        return;
                    }
                    if (name.equalsIgnoreCase("local")) {
                        if (!player.hasPermission("headdb.local")) {
                            Utils.sendMessage(player, "&cYou do not have permission to view local heads!");
                            player.closeInventory();
                            return;
                        }
                        InventoryUtils.openLocalMenu(player);
                        return;
                    }
                    if (name.equalsIgnoreCase("search")) {
                        new AnvilGUI.Builder()
                                .onComplete((p, text) -> {
                                    InventoryUtils.openSearchDatabase(p, text);
                                    return AnvilGUI.Response.openInventory(InventoryUtils.openSearchDatabase(p, text).getInventory());
                                })
                                .title("Search Heads")
                                .text("Name...")
                                .plugin(HeadDB.getInstance())
                                .open(player);
                        return;
                    }

                    Category category = Category.getByName(name);

                    if (category != null) {
                        InventoryUtils.openCategoryDatabase(player, category);
                    }
                }
            }
        }
    }

}
