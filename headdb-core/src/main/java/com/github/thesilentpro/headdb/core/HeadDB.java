package com.github.thesilentpro.headdb.core;

import com.github.thesilentpro.grim.listener.PageListeners;
import com.github.thesilentpro.grim.page.registry.PageRegistry;
import com.github.thesilentpro.headdb.api.HeadAPI;
import com.github.thesilentpro.headdb.api.HeadDatabase;
import com.github.thesilentpro.headdb.api.model.Head;
import com.github.thesilentpro.headdb.core.command.HDBMainCommand;
import com.github.thesilentpro.headdb.core.command.HDBSubCommandManager;
import com.github.thesilentpro.headdb.core.config.Config;
import com.github.thesilentpro.headdb.core.economy.EconomyProvider;
import com.github.thesilentpro.headdb.core.economy.VaultEconomyProvider;
import com.github.thesilentpro.headdb.core.menu.MenuManager;
import com.github.thesilentpro.headdb.core.storage.PlayerStorage;
import com.github.thesilentpro.headdb.core.util.HDBLocalization;
import com.github.thesilentpro.headdb.implementation.BaseHeadAPI;
import com.github.thesilentpro.headdb.implementation.BaseHeadDatabase;
import com.github.thesilentpro.headdb.core.util.Compatibility;
import com.github.thesilentpro.headdb.core.util.Utils;
import com.github.thesilentpro.inputs.paper.PaperInputListener;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.BiConsumer;

public class HeadDB extends JavaPlugin {

    // Avoid using class for this logger: it will use the fully qualified name with the default logger cfg.
    private static final Logger LOGGER = LoggerFactory.getLogger("HeadDB");

    private Config config;
    private HeadDatabase headDatabase;
    private HeadAPI headApi;
    private HDBSubCommandManager subCommandManager;
    private MenuManager menuManager;
    private PlayerStorage playerStorage;
    private HDBLocalization localization;
    private EconomyProvider economyProvider;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.config = new Config(this);
        this.config.load();
        this.localization = new HDBLocalization(this);
        this.localization.init();
        String econProvider = this.config.getEconomyProvider();
        if (econProvider != null) {
            if (econProvider.equalsIgnoreCase("NONE") || econProvider.isEmpty()) {
                LOGGER.debug("Economy is disabled.");
            } else if (this.config.getEconomyProvider().equalsIgnoreCase("VAULT")) {
                this.economyProvider = new VaultEconomyProvider();
                this.economyProvider.init();
                LOGGER.debug("Economy Provider: Vault");
            } else {
                LOGGER.warn("Unknown economy provider in the config.yml!");
            }
        }

        // Init database
        int databaseThreads = this.config.getDatabaseThreads();
        this.headDatabase = new BaseHeadDatabase(Utils.executorService(databaseThreads, "Head Database Worker"), this.config.resolveEnabledIndexes());
        this.headDatabase.update().thenAcceptAsync(heads -> DATABASE_UPDATE_ACTION.accept(this.config, heads), Compatibility.getMainThreadExecutor(this));
        this.headApi = new BaseHeadAPI(this.config.getApiThreads(), headDatabase);
        this.menuManager = new MenuManager(this);
        this.headDatabase.onReady().thenRunAsync(() -> this.menuManager.registerDefaults(this));
        this.playerStorage = new PlayerStorage();
        this.playerStorage.load();
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> this.playerStorage.save(), this.config.getPlayerStorageSaveInterval() * 20L, this.config.getPlayerStorageSaveInterval() * 20L);

        getServer().getServicesManager().register(HeadAPI.class, headApi, this, ServicePriority.Normal);

        // Command handling
        this.subCommandManager = new HDBSubCommandManager(this);
        this.subCommandManager.registerDefaults();

        PluginCommand command = getCommand("headdb");
        if (command == null) {
            throw new RuntimeException("Missing HeadDB command in plugin.yml"); // Should never reach this
        }

        HDBMainCommand mainCommand = new HDBMainCommand(this);
        command.setExecutor(mainCommand);
        command.setTabCompleter(mainCommand);

        new PageListeners().register(this);
        new PaperInputListener().register(this);

        // Start updater task
        if (this.config.isUpdaterEnabled()) {
            this.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> this.headDatabase.update().thenAccept(heads -> DATABASE_UPDATE_ACTION.accept(this.config, heads)), 86400L * 20L, 86400L * 20L);
        }

        if (!Compatibility.IS_PAPER) {
            String bukkitName = Bukkit.getName();
            String bukkitVersion = Bukkit.getBukkitVersion().substring(0, Bukkit.getBukkitVersion().indexOf("-"));
            LOGGER.warn("""
                    \s
                    \s
                        You're running a non-Paper server implementation (detected: {}).
                        For best performance and full compatibility with this plugin, it is highly recommended switching to Paper.
                        > Download it here: https://papermc.io/downloads
                    \s
                    """, bukkitName + " " + bukkitVersion);
        }

        LOGGER.info("Done! Database is {}", !this.headDatabase.isReady() ? "loading..." : "ready");
    }

    @Override
    public void onDisable() {
        PageRegistry.INSTANCE.getPages().keySet().forEach(InventoryView::close);
        if (this.playerStorage != null) {
            this.playerStorage.save();
        }
    }

    public EconomyProvider getEconomyProvider() {
        return economyProvider;
    }

    public HDBLocalization getLocalization() {
        return localization;
    }

    public PlayerStorage getPlayerStorage() {
        return playerStorage;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public HDBSubCommandManager getSubCommandManager() {
        return subCommandManager;
    }

    public Config getCfg() {
        return config;
    }

    public HeadAPI getHeadApi() {
        return headApi;
    }

    private static final BiConsumer<Config, List<Head>> DATABASE_UPDATE_ACTION = (config, heads) -> {
        LOGGER.info("Loaded {} heads!", heads.size());

        if (config.isPreloadHeads()) {
            int total = heads.size();
            if (total == 0) {
                LOGGER.info("No heads to preload.");
                return;
            }

            int[] milestones = {25, 50, 75, 100};
            int nextMilestoneIndex = 0;

            for (int i = 0; i < total; i++) {
                Head head = heads.get(i);
                head.getItem(); // Loads the ItemStack in memory

                int percent = (int) (((i + 1) / (double) total) * 100);
                if (nextMilestoneIndex < milestones.length && percent >= milestones[nextMilestoneIndex]) {
                    LOGGER.info("Preloading heads... {}%", milestones[nextMilestoneIndex]);
                    nextMilestoneIndex++;
                }
            }
        }
    };

}