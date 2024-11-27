package tsp.headdb;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tsp.headdb.core.commands.CommandManager;
import tsp.headdb.core.player.PlayerDatabase;
import tsp.headdb.core.config.ConfigData;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.core.economy.EconomyProvider;
import tsp.headdb.core.economy.VaultProvider;
import tsp.headdb.api.model.Head;
import tsp.headdb.core.storage.Storage;
import tsp.headdb.core.util.Localization;
import tsp.invlib.InvLib;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author TheSilentPro (Silent)
 */
public class HeadDB extends JavaPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeadDB.class);
    private static HeadDB instance;
    private ConfigData config;
    private EconomyProvider economyProvider;
    private boolean PAPI;
    private PlayerDatabase playerDatabase;
    private Localization localization;
    private Storage storage;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.config = new ConfigData(getConfig());
        this.playerDatabase = new PlayerDatabase();
        this.storage = new Storage().init();
        this.playerDatabase.load();
        LOGGER.info("Loaded {} languages!", loadLocalization());

        InvLib.init(this);

        this.PAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        if (this.config.isEconomyEnabled()) {
            if (this.config.getEconomyProvider().equalsIgnoreCase("VAULT")) {
                this.economyProvider = new VaultProvider();
            } else {
                LOGGER.error("Invalid economy provider in config.yml!");
                this.setEnabled(false);
                return;
            }

            this.economyProvider.init();
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::updateDatabase, 0L, 86400 * 20);

        this.commandManager = new CommandManager().init();
        PluginCommand mainCommand = getCommand("headdb");
        if (mainCommand == null) {
            LOGGER.error("Failed to get main /headdb command!");
            this.setEnabled(false);
            return;
        }
        mainCommand.setExecutor(commandManager);
        mainCommand.setTabCompleter(commandManager);
    }

    @Override
    public void onDisable() {
        // Save language data
        if (playerDatabase != null) {
            playerDatabase.save();
        }
    }

    public CompletableFuture<List<Head>> updateDatabase() {
        LOGGER.info("Fetching heads...");
        long fetchStart = System.currentTimeMillis();
        return HeadAPI.getDatabase().getHeadsNoCache().thenApply(result -> {
            LOGGER.info("Fetched {} total heads! ({}s)", HeadAPI.getTotalHeads(), TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - fetchStart));

            long preloadStart = System.currentTimeMillis();
            int total = result.size();
            int index = 0;

            LOGGER.info("Preloading {} heads...", total);
            // Milestone percentages we want to print
            int[] milestones = {25, 50, 75, 100};
            int nextMilestoneIndex = 0;

            for (Head head : result) {
                // Simulate processing each head
                head.getItem();
                index++;

                // Calculate percentage completion
                int progress = (int) ((index / (double) total) * 100);

                // Check if the current progress matches the next milestone
                if (nextMilestoneIndex < milestones.length && progress >= milestones[nextMilestoneIndex]) {
                    LOGGER.info("Preloading heads... {}%", progress);
                    nextMilestoneIndex++; // Move to the next milestone
                }
            }
            LOGGER.info("Preloaded {} total heads! ({}s)", index, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - preloadStart));
            return result;
        });
    }

    private int loadLocalization() {
        instance.localization = new Localization(this, "messages");
        try {
            instance.localization.createDefaults();
            return instance.localization.load();
        } catch (URISyntaxException | IOException ex) {
            LOGGER.error("Failed to load localization!", ex);
            this.setEnabled(false);
            return 0;
        }
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Storage getStorage() {
        return storage;
    }

    public PlayerDatabase getPlayerDatabase() {
        return playerDatabase;
    }

    public Localization getLocalization() {
        return localization;
    }

    public boolean isPAPI() {
        return PAPI;
    }

    public EconomyProvider getEconomyProvider() {
        return economyProvider;
    }

    @NotNull
    public ConfigData getCfg() {
        return config;
    }

    public static HeadDB getInstance() {
        return instance;
    }

}