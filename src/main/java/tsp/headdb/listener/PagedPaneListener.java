package tsp.headdb.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import tsp.headdb.inventory.PagedPane;

/**
 * Listens for click events for the {@link PagedPane}
 */
public class PagedPaneListener implements Listener {

    public PagedPaneListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof PagedPane) {
            ((PagedPane) holder).onClick(event);
        }
    }
}
