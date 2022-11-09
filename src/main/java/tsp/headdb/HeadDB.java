package tsp.headdb;

import tsp.headdb.core.command.CommandCategory;
import tsp.headdb.core.command.CommandGive;
import tsp.headdb.core.command.CommandHelp;
import tsp.headdb.core.command.CommandInfo;
import tsp.headdb.core.command.CommandLanguage;
import tsp.headdb.core.command.CommandMain;
import tsp.headdb.core.command.CommandManager;
import tsp.headdb.core.command.CommandSearch;

import tsp.headdb.core.command.CommandSettings;
import tsp.headdb.core.command.CommandTexture;
import tsp.headdb.core.command.CommandUpdate;
import tsp.headdb.core.economy.BasicEconomyProvider;
import tsp.headdb.core.economy.VaultProvider;
import tsp.headdb.core.listener.PlayerJoinListener;
import tsp.headdb.core.storage.Storage;
import tsp.headdb.core.task.UpdateTask;

import tsp.headdb.core.util.BuildProperties;
import tsp.smartplugin.SmartPlugin;
import tsp.smartplugin.inventory.PaneListener;
import tsp.smartplugin.localization.TranslatableLocalization;
import tsp.smartplugin.logger.PluginLogger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

public class HeadDB extends SmartPlugin {

    private static HeadDB instance;
    private PluginLogger logger;
    private BuildProperties buildProperties;
    private TranslatableLocalization localization;
    private BasicEconomyProvider economyProvider;
    private CommandManager commandManager;
    private Storage storage;

    @Override
    public void onStart() {
        instance = this;
        instance.saveDefaultConfig();
        instance.logger = new PluginLogger(this, getConfig().getBoolean("debug"));
        instance.logger.info("Loading HeadDB - " + instance.getDescription().getVersion());
        instance.buildProperties = new BuildProperties(this);

        new UpdateTask(getConfig().getLong("refresh", 86400L)).schedule(this);
        instance.logger.info("Loaded " + loadLocalization() + " languages!");

        instance.initEconomy();

        instance.storage = new Storage();
        //instance.storage.load();

        new PaneListener(this);
        new PlayerJoinListener();

        instance.commandManager = new CommandManager();
        loadCommands();
        //noinspection ConstantConditions
        instance.getCommand("headdb").setExecutor(new CommandMain());

        new Metrics(this, 9152);
        instance.logger.info("Done!");
    }

    @Override
    public void onDisable() {
        //instance.storage.save();
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
        new CommandHelp().register();
        new CommandCategory().register();
        new CommandSearch().register();
        new CommandGive().register();
        new CommandUpdate().register();
        new CommandTexture().register();
        new CommandLanguage().register();
        new CommandSettings().register();
        new CommandInfo().register();
    }

    public Storage getStorage() {
        return storage;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Optional<BasicEconomyProvider> getEconomyProvider() {
        return Optional.ofNullable(economyProvider);
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
