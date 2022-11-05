package tsp.headdb.core.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tsp.headdb.HeadDB;
import tsp.headdb.core.storage.PlayerData;

public final class PlayerJoinListener implements Listener {

    public PlayerJoinListener() {
        Bukkit.getPluginManager().registerEvents(this, HeadDB.getInstance());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // todo: favorites
        HeadDB.getInstance().getStorage().getPlayerStorage().register(new PlayerData(event.getPlayer().getUniqueId(), ""));
    }

}
