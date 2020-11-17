package tsp.headdb;

import org.bukkit.plugin.java.JavaPlugin;
import tsp.headdb.command.Command_headdb;
import tsp.headdb.database.HeadDatabase;
import tsp.headdb.listener.JoinListener;
import tsp.headdb.listener.PagedPaneListener;
import tsp.headdb.listener.MenuListener;
import tsp.headdb.util.Config;
import tsp.headdb.util.Log;
import tsp.headdb.util.Metrics;
import tsp.headdb.util.Utils;

public class HeadDB extends JavaPlugin {

    private static HeadDB instance;
    private static Config config;
    private static Config playerdata;

    @Override
    public void onEnable() {
        instance = this;
        Log.info("Loading HeadDB - " + getDescription().getVersion());
        saveDefaultConfig();
        config = new Config("plugins/HeadDB/config.yml");
        playerdata = new Config("plugins/HeadDB/playerdata.yml");
        playerdata.create();

        Log.debug("Starting metrics...");
        new Metrics(this, Utils.METRICS_ID);

        Log.debug("Registering listeners...");
        new PagedPaneListener(this);
        new MenuListener(this);
        new JoinListener(this);

        Log.debug("Registering commands...");
        getCommand("headdb").setExecutor(new Command_headdb());

        Log.debug("Initializing Database...");
        HeadDatabase.update();

        Log.info("Done!");
    }

    public static Config getCfg() {
        return config;
    }

    public static Config getPlayerdata() {
        return playerdata;
    }

    public static HeadDB getInstance() {
        return instance;
    }

}
