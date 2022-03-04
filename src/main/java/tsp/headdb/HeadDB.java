package tsp.headdb;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.command.HeadDBCommand;
import tsp.headdb.economy.TreasuryProvider;
import tsp.headdb.implementation.DataSaveTask;
import tsp.headdb.implementation.DatabaseUpdateTask;
import tsp.headdb.economy.BasicEconomyProvider;
import tsp.headdb.economy.VaultProvider;
import tsp.headdb.listener.JoinListener;
import tsp.headdb.listener.MenuListener;
import tsp.headdb.listener.PagedPaneListener;
import tsp.headdb.storage.PlayerDataFile;
import tsp.headdb.util.Localization;
import tsp.headdb.util.Log;
import tsp.headdb.util.Utils;

import javax.annotation.Nullable;
import java.io.File;

/**
 * Main class of HeadDB
 */
public class HeadDB extends JavaPlugin {

    private static HeadDB instance;
    private BasicEconomyProvider economyProvider;
    private PlayerDataFile playerData;
    private Localization localization;

    @Override
    public void onEnable() {
        instance = this;
        Log.info("Loading HeadDB - " + getDescription().getVersion());
        saveDefaultConfig();
        createLocalizationFile();

        this.playerData = new PlayerDataFile("player_data.json");
        this.playerData.load();

        if (getConfig().getBoolean("economy.enable")) {
            String rawProvider = getConfig().getString("economy.provider", "VAULT");
            Log.debug("Starting economy with provider: " + rawProvider);
            if (rawProvider.equalsIgnoreCase("vault")) {
                economyProvider = new VaultProvider();
                economyProvider.initProvider();
            } else if (rawProvider.equalsIgnoreCase("treasury")) {
                economyProvider = new TreasuryProvider();
                economyProvider.initProvider();
            }
        }

        long refresh = getConfig().getLong("refresh") * 20;
        HeadAPI.getDatabase().setRefresh(refresh);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new DatabaseUpdateTask(), 0, refresh);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new DataSaveTask(), refresh, refresh);

        new JoinListener(this);
        new MenuListener(this);
        new PagedPaneListener(this);

        getCommand("headdb").setExecutor(new HeadDBCommand());

        Log.debug("Starting metrics...");
        initMetrics();

        Utils.isLatestVersion(this, 84967, latest -> {
            if (!latest) {
                Log.warning("There is a new update available for HeadDB on spigot!");
                Log.warning("Download: https://www.spigotmc.org/resources/84967");
            }
        });

        Log.info("Done!");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        this.playerData.save();
    }

    public Localization getLocalization() {
        return localization;
    }

    public PlayerDataFile getPlayerData() {
        return playerData;
    }

    @Nullable
    public BasicEconomyProvider getEconomyProvider() {
        return economyProvider;
    }

    public static HeadDB getInstance() {
        return instance;
    }

    private void createLocalizationFile() {
        File messagesFile = new File(getDataFolder().getAbsolutePath() + "/messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
            Log.debug("Localization loaded from jar file.");
        }

        this.localization = new Localization(messagesFile);
        this.localization.load();
    }

    private void initMetrics() {
        Metrics metrics = new Metrics(this, 9152);
        if (!metrics.isEnabled()) {
            Log.debug("Metrics are disabled.");
            return;
        }

        metrics.addCustomChart(new Metrics.SimplePie("economy_provider", () -> {
            if (this.getEconomyProvider() != null) {
                return this.getConfig().getString("economy.provider");
            }

            return "None";
        }));
    }


}
