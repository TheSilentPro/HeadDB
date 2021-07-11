package tsp.headdb.util;

import de.leonhard.storage.Config;
import de.leonhard.storage.Json;
import de.leonhard.storage.LightningBuilder;
import org.bukkit.plugin.java.JavaPlugin;

public class Storage {

    private Config config;
    private Json playerdata;

    public Storage init(JavaPlugin plugin) {
        config = LightningBuilder.fromPath("config.yml", plugin.getDataFolder().getAbsolutePath()).createConfig().addDefaultsFromInputStream();
        playerdata = LightningBuilder.fromPath("playerdata.json", plugin.getDataFolder().getAbsolutePath()).createJson();
        return this;
    }

    public Config getConfig() {
        return config;
    }

    public Json getPlayerData() {
        return playerdata;
    }

}
