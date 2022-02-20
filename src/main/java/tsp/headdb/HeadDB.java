package tsp.headdb;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.command.CommandHeadDB;
import tsp.headdb.database.DatabaseUpdateTask;
import tsp.headdb.economy.HEconomyProvider;
import tsp.headdb.economy.VaultProvider;
import tsp.headdb.listener.JoinListener;
import tsp.headdb.listener.MenuListener;
import tsp.headdb.listener.PagedPaneListener;
import tsp.headdb.storage.PlayerDataFile;
import tsp.headdb.util.Localization;
import tsp.headdb.util.Log;
import tsp.headdb.util.Metrics;
import tsp.headdb.util.Utils;

import javax.annotation.Nullable;
import java.io.File;

public class HeadDB extends JavaPlugin {

    private static HeadDB instance;
    private HEconomyProvider economyProvider;
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
            }
        }

        long refresh = getConfig().getLong("refresh") * 20;
        HeadAPI.getDatabase().updateAsync(heads -> Log.info("Fetched " + HeadAPI.getHeads().size() + " heads!")); // Update database on startup
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new DatabaseUpdateTask(), refresh, refresh); // Update database on set interval (also saves data)
        HeadAPI.getDatabase().setRefresh(refresh);

        new JoinListener(this);
        new MenuListener(this);
        new PagedPaneListener(this);

        getCommand("headdb").setExecutor(new CommandHeadDB());

        Log.debug("Starting metrics...");
        new Metrics(this, 9152);

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
        this.playerData.save();
    }

    public Localization getLocalization() {
        return localization;
    }

    public PlayerDataFile getPlayerData() {
        return playerData;
    }

    @Nullable
    public HEconomyProvider getEconomyProvider() {
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


}
