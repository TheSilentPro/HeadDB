package tsp.headdb;

import de.leonhard.storage.Config;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.command.Command_headdb;
import tsp.headdb.listener.JoinListener;
import tsp.headdb.listener.PagedPaneListener;
import tsp.headdb.listener.MenuListener;
import tsp.headdb.util.Log;
import tsp.headdb.util.Metrics;
import tsp.headdb.util.Storage;
import tsp.headdb.util.Utils;
import org.bukkit.plugin.RegisteredServiceProvider;
import net.milkbowl.vault.economy.Economy;

public class HeadDB extends JavaPlugin {

    private static HeadDB instance;
    private Storage storage;
    private Economy economy = null;

    @Override
    public void onEnable() {
        instance = this;
        Log.info("Loading HeadDB - " + getDescription().getVersion());
        saveDefaultConfig();
        storage = new Storage().init(this);

        if (storage.getConfig().getBoolean("economy.enable")) {
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

        if (storage.getConfig().getBoolean("fetchStartup")) {
            if (storage.getConfig().getBoolean("asyncStartup")) {
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

    public Config getConfiguration() {
        return storage.getConfig();
    }

    public Storage getStorage() {
        return storage;
    }

    public Economy getEconomy() {
        return this.economy;
    }

    public static HeadDB getInstance() {
        return instance;
    }

}
