package tsp.headdb.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import tsp.headdb.database.Category;
import tsp.headdb.inventory.InventoryUtils;
import tsp.headdb.util.Utils;
import tsp.headdb.util.XMaterial;

public class MenuListener implements Listener {

    public MenuListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        if (e.getView().getTitle().startsWith(Utils.colorize("&c&lHeadDB"))) {
            e.setCancelled(true);
            Inventory inventory = e.getClickedInventory();

            if (inventory != null) {
                int slot = e.getSlot();
                ItemStack item = inventory.getItem(slot);

                if (item != null && item.getType() != XMaterial.AIR.parseMaterial()) {
                    String name = ChatColor.stripColor(item.getItemMeta().getDisplayName().toLowerCase());
                    Category category = Category.getByName(name);

                    if (category != null) {
                        InventoryUtils.openCategoryDatabase((Player) e.getWhoClicked(), category);

                    }
                }
            }
        }
    }

}
