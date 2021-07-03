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
import org.bukkit.plugin.RegisteredServiceProvider;
import net.milkbowl.vault.economy.Economy;

public class HeadDB extends JavaPlugin {

    private static HeadDB instance;
    private Config config;
    private Json playerdata;
    private Economy economy = null;

    @Override
    public void onEnable() {
        instance = this;
        Log.info("Loading HeadDB - " + getDescription().getVersion());
        saveDefaultConfig();
        config = LightningBuilder.fromPath("config.yml", "plugins/HeadDB").createConfig().addDefaultsFromInputStream();
        playerdata = LightningBuilder.fromPath("playerdata.json", "plugins/HeadDB").createJson();

        if (config.getBoolean("economy.enable"))
        {
            Log.debug("Starting economy...");
            this.economy = this.setupEconomy();
            if (this.economy == null) {
                Log.error("Economy support requires Vault and an economy provider plugin.");
            } else {
                Log.info("Economy provider: " + this.economy.getName());
            }
        }

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
                Bukkit.getScheduler().runTaskAsynchronously(this, () -> HeadAPI.getDatabase().update());
            } else {
                Log.debug("Initializing Database... (SYNC)");
                HeadAPI.getDatabase().update();
            }
        }

        Log.info("Done!");
    }

    private Economy setupEconomy() {
        if (!this.getServer().getPluginManager().isPluginEnabled("Vault")) return null;

        RegisteredServiceProvider<Economy> economyProvider = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) return null;

        return this.economy = economyProvider.getProvider();
    }

    public Config getCfg() {
        return config;
    }

    public Json getPlayerdata() {
        return playerdata;
    }

    public static HeadDB getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return this.economy;
    }
}
