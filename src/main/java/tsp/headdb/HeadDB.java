package tsp.headdb;

import org.bukkit.command.PluginCommand;
import tsp.headdb.core.command.CommandCategory;
import tsp.headdb.core.command.CommandGive;
import tsp.headdb.core.command.CommandHelp;
import tsp.headdb.core.command.CommandInfo;
import tsp.headdb.core.command.CommandLanguage;
import tsp.headdb.core.command.CommandMain;
import tsp.headdb.core.command.CommandManager;
import tsp.headdb.core.command.CommandReload;
import tsp.headdb.core.command.CommandSearch;

import tsp.headdb.core.command.CommandSettings;
import tsp.headdb.core.command.CommandTexture;
import tsp.headdb.core.command.CommandUpdate;
import tsp.headdb.core.economy.BasicEconomyProvider;
import tsp.headdb.core.economy.VaultProvider;
import tsp.headdb.core.storage.Storage;
import tsp.headdb.core.task.UpdateTask;

import tsp.headdb.core.util.BuildProperties;
import tsp.smartplugin.SmartPlugin;
import tsp.smartplugin.inventory.PaneListener;
import tsp.smartplugin.localization.TranslatableLocalization;
import tsp.smartplugin.logger.PluginLogger;
import tsp.smartplugin.utils.PluginUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Optional;

public class HeadDB extends SmartPlugin {

    private static HeadDB instance;
    private PluginLogger logger;
    private BuildProperties buildProperties;
    private TranslatableLocalization localization;
    private Storage storage;
    private BasicEconomyProvider economyProvider;
    private CommandManager commandManager;

    @Override
    public void onStart() {
        instance = this;
        instance.saveDefaultConfig();
        instance.logger = new PluginLogger(this, getConfig().getBoolean("debug"));
        instance.logger.info("Loading HeadDB - " + instance.getDescription().getVersion());
        instance.buildProperties = new BuildProperties(this);

        new UpdateTask(getConfig().getLong("refresh", 86400L)).schedule(this);
        instance.logger.info("Loaded " + loadLocalization() + " languages!");

        instance.initStorage();
        instance.initEconomy();

        new PaneListener(this);
        //new PlayerJoinListener();

        instance.commandManager = new CommandManager();
        loadCommands();

        initMetrics();
        ensureLatestVersion();
        instance.logger.info("Done!");
    }

    @Override
    public void onDisable() {
        if (storage != null) {
            storage.getPlayerStorage().suspend();
        }
    }

    private void initMetrics() {
        Metrics metrics = new Metrics(this, 9152);

        metrics.addCustomChart(new Metrics.SimplePie("economy_provider", () -> {
            if (getEconomyProvider().isPresent()) {
                return this.getConfig().getString("economy.provider");
            }

            return "None";
        }));
    }

    private void ensureLatestVersion() {
        PluginUtils.isLatestVersion(this, 84967, latest -> {
            if (Boolean.FALSE.equals(latest)) {
                instance.logger.warning("There is a new update available for HeadDB on spigot!");
                instance.logger.warning("Download: https://www.spigotmc.org/resources/84967");
            }
        });
    }

    // Loaders

    private void initStorage() {
        storage = new Storage(getConfig().getInt("storage.threads"));
        storage.getPlayerStorage().init();
    }

    private int loadLocalization() {
        instance.localization = new TranslatableLocalization(this, "messages");
        try {
            instance.localization.createDefaults();
            return instance.localization.load();
        } catch (URISyntaxException | IOException ex) {
            instance.logger.error("Failed to load localization!");
            ex.printStackTrace();
            this.setEnabled(false);
            return 0;
        }
    }

    private void initEconomy() {
        if (!getConfig().getBoolean("economy.enabled")) {
            instance.logger.debug("Economy disabled by config.yml!");
            instance.economyProvider = null;
            return;
        }

        String raw = getConfig().getString("economy.provider", "VAULT");
        if (raw.equalsIgnoreCase("VAULT")) {
            economyProvider = new VaultProvider();
        }

        economyProvider.init();
        instance.logger.info("Economy Provider: " + raw);
    }

    private void loadCommands() {
        PluginCommand main = getCommand("headdb");
        if (main != null) {
            main.setExecutor(new CommandMain());
            main.setTabCompleter(new CommandMain());
        } else {
            instance.logger.error("Could not find main 'headdb' command!");
            this.setEnabled(false);
            return;
        }

        new CommandHelp().register();
        new CommandCategory().register();
        new CommandSearch().register();
        new CommandGive().register();
        new CommandUpdate().register();
        new CommandReload().register();
        new CommandTexture().register();
        new CommandLanguage().register();
        new CommandSettings().register();
        new CommandInfo().register();
    }

    // Getters

    public Storage getStorage() {
        return storage;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Optional<BasicEconomyProvider> getEconomyProvider() {
        return Optional.ofNullable(economyProvider);
    }

    @SuppressWarnings("ConstantConditions")
    private DecimalFormat decimalFormat = new DecimalFormat(getConfig().getString("economy.format"));

    public DecimalFormat getDecimalFormat() {
        return decimalFormat != null ? decimalFormat : (decimalFormat = new DecimalFormat("##.##"));
    }

    public TranslatableLocalization getLocalization() {
        return localization;
    }

    public BuildProperties getBuildProperties() {
        return buildProperties;
    }

    public PluginLogger getLog() {
        return logger;
    }

    public static HeadDB getInstance() {
        return instance;
    }

}
