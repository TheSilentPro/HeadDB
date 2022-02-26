package tsp.headdb.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tsp.headdb.HeadDB;
import tsp.headdb.storage.PlayerDataFile;

/**
 * This saves heads from players that join
 * Used for local heads option
 */
public class JoinListener implements Listener {

    public JoinListener(HeadDB plugin) {
        // If local heads are disabled, there is no need for this listener.
        if (HeadDB.getInstance().getConfig().getBoolean("localHeads")) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        HeadDB.getInstance().getPlayerData().modifyUsername(e.getPlayer().getUniqueId(), e.getPlayer().getName(), PlayerDataFile.ModificationType.SET);
        HeadDB.getInstance().getPlayerData().save();
    }

}
