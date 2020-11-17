package tsp.headdb.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tsp.headdb.HeadDB;

public class JoinListener implements Listener {

    public JoinListener(HeadDB plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        HeadDB.getPlayerdata().set(e.getPlayer().getUniqueId().toString() + ".username", e.getPlayer().getName());
        HeadDB.getPlayerdata().save();
    }

}
