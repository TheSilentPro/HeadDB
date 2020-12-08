package tsp.headdb;

import de.leonhard.storage.Config;
import de.leonhard.storage.Json;
import de.leonhard.storage.LightningBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.command.Command_headdb;
import tsp.headdb.listener.JoinListener;
import tsp.headdb.listener.PagedPaneListener;
import tsp.headdb.listener.MenuListener;
import tsp.headdb.util.Log;
import tsp.headdb.util.Metrics;
import tsp.headdb.util.Utils;

public class HeadDB extends JavaPlugin {

    private static HeadDB instance;
    private static Config config;
    private static Json playerdata;

    @Override
    public void onEnable() {
        instance = this;
        Log.info("Loading HeadDB - " + getDescription().getVersion());
        saveDefaultConfig();
        config = LightningBuilder.fromPath("config.yml", "plugins/HeadDB").createConfig().addDefaultsFromInputStream();
        playerdata = LightningBuilder.fromPath("playerdata.json", "plugins/HeadDB").createJson();

        Log.debug("Starting metrics...");
        new Metrics(this, Utils.METRICS_ID);

        Log.debug("Registering listeners...");
        new PagedPaneListener(this);
        new MenuListener(this);
        new JoinListener(this);

        Log.debug("Registering commands...");
        getCommand("headdb").setExecutor(new Command_headdb());

        if (config.getBoolean("fetchStartup")) {
            if (config.getBoolean("asyncStartup")) {
                Log.debug("Initializing Database... (ASYNC)");
                Bukkit.getScheduler().runTaskAsynchronously(this, task -> HeadAPI.getDatabase().update());
            }else {
                Log.debug("Initializing Database... (SYNC)");
                Bukkit.getScheduler().runTask(this, task -> HeadAPI.getDatabase().update());
            }
        }

        Log.info("Done!");
    }

    public static Config getCfg() {
        return config;
    }

    public static Json getPlayerdata() {
        return playerdata;
    }

    public static HeadDB getInstance() {
        return instance;
    }

}
