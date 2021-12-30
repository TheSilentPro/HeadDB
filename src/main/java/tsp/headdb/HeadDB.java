package tsp.headdb;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.command.CommandHeadDB;
import tsp.headdb.listener.JoinListener;
import tsp.headdb.listener.MenuListener;
import tsp.headdb.listener.PagedPaneListener;
import tsp.headdb.storage.PlayerDataFile;
import tsp.headdb.util.Log;
import tsp.headdb.util.Metrics;

public class HeadDB extends JavaPlugin {

    private static HeadDB instance;
    private Economy economy;
    private PlayerDataFile playerData;

    @Override
    public void onEnable() {
        instance = this;
        Log.info("Loading HeadDB - " + getDescription().getVersion());
        saveDefaultConfig();

        this.playerData = new PlayerDataFile("player_data.json");
        this.playerData.load();

        if (getConfig().getBoolean("economy.enable")) {
            Log.debug("Starting economy...");
            this.economy = this.setupEconomy();
            if (this.economy == null) {
                Log.error("Economy support requires Vault and an economy provider plugin.");
            } else {
                Log.info("Economy provider: " + this.economy.getName());
            }
        }

        long refresh = getConfig().getLong("refresh") * 20;
        HeadAPI.getDatabase().setRefresh(refresh);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, task ->
                HeadAPI.getDatabase().updateAsync(heads -> Log.info("Fetched " + HeadAPI.getHeads().size() + " heads!")),
                0L, refresh);

        new JoinListener(this);
        new MenuListener(this);
        new PagedPaneListener(this);

        getCommand("headdb").setExecutor(new CommandHeadDB());

        Log.debug("Starting metrics...");
        new Metrics(this, 9152);
        Log.info("Done!");
    }

    @Override
    public void onDisable() {
        this.playerData.save();
    }

    private Economy setupEconomy() {
        if (!this.getServer().getPluginManager().isPluginEnabled("Vault")) return null;

        RegisteredServiceProvider<Economy> economyProvider = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) return null;

        return this.economy = economyProvider.getProvider();
    }

    public Economy getEconomy() {
        return economy;
    }

    public PlayerDataFile getPlayerData() {
        return playerData;
    }

    public static HeadDB getInstance() {
        return instance;
    }

}
