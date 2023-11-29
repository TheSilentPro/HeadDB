package tsp.headdb;

import org.bukkit.command.PluginCommand;
import tsp.headdb.core.command.*;
import tsp.headdb.core.economy.BasicEconomyProvider;
import tsp.headdb.core.economy.VaultProvider;
import tsp.headdb.core.storage.Storage;
import tsp.headdb.core.task.UpdateTask;
import tsp.headdb.core.util.HeadDBLogger;
import tsp.headdb.core.util.Utils;
import tsp.helperlite.HelperLite;
import tsp.helperlite.Schedulers;
import tsp.helperlite.scheduler.promise.Promise;
import tsp.helperlite.scheduler.task.Task;
import tsp.nexuslib.NexusPlugin;
import tsp.nexuslib.inventory.PaneListener;
import tsp.nexuslib.localization.TranslatableLocalization;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class HeadDB extends NexusPlugin {

    private static HeadDB instance;
    private HeadDBLogger logger;
    private TranslatableLocalization localization;
    private Storage storage;
    private BasicEconomyProvider economyProvider;
    private CommandManager commandManager;
    private Task updateTask;

    @Override
    public void onStart(NexusPlugin nexusPlugin) {
        instance = this;
        HelperLite.init(this);

        instance.saveDefaultConfig();
        instance.logger = new HeadDBLogger(getConfig().getBoolean("debug"));
        instance.logger.info("Loading HeadDB - " + Utils.getVersion().orElse(getDescription().getVersion() + " (UNKNOWN SEMVER)"));

        instance.logger.info("Loaded " + loadLocalization() + " languages!");

        instance.initStorage();
        instance.initEconomy();

        startUpdateTask();

        new PaneListener(this);

        // TODO: Commands helperlite
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
            File langFile = new File(getDataFolder(), "langs.data");
            if (!langFile.exists()) {
                try {
                    //noinspection ResultOfMethodCallIgnored
                    langFile.createNewFile();
                    localization.saveLanguages(langFile);
                } catch (IOException ex) {
                    logger.error("Failed to save receiver langauges!");
                    ex.printStackTrace();
                }
            }
        }

        updateTask.stop();
    }

    private void startUpdateTask() {
        updateTask = Schedulers.builder()
                .async()
                .every(getConfig().getLong("refresh", 86400L), TimeUnit.SECONDS)
                .run(new UpdateTask());
    }

    private void ensureLatestVersion() {
        Promise.start().thenApplyAsync(a -> {
            try {
                URLConnection connection = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + 84967).openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestProperty("User-Agent", this.getName() + "-VersionChecker");

                return new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine().equals(Utils.getVersion().orElse(getDescription().getVersion()));
            } catch (IOException ex) {
                return false;
            }
        }).thenAcceptAsync(latest -> {
            if (latest) {
                instance.logger.warning("There is a new update available for HeadDB on spigot!");
                instance.logger.warning("Download: https://www.spigotmc.org/resources/84967");
            }
        });
    }

    // Loaders

    private void initMetrics() {
        Metrics metrics = new Metrics(this, 9152);

        metrics.addCustomChart(new Metrics.SimplePie("economy_provider", () -> {
            if (getEconomyProvider().isPresent()) {
                return this.getConfig().getString("economy.provider");
            }

            return "None";
        }));
    }

    private void initStorage() {
        storage = new Storage();
        storage.getPlayerStorage().init();
    }

    private int loadLocalization() {
        instance.localization = new TranslatableLocalization(this, "messages");
        try {
            instance.localization.createDefaults();
            int count = instance.localization.load();
            File langFile = new File(getDataFolder(), "langs.data");
            if (langFile.exists()) {
                localization.loadLanguages(langFile);
            }

            return count;
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

    public Optional<Task> getUpdateTask() {
        return Optional.ofNullable(updateTask);
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

    @SuppressWarnings("DataFlowIssue")
    private DecimalFormat decimalFormat = new DecimalFormat(getConfig().getString("economy.format"));

    public DecimalFormat getDecimalFormat() {
        return decimalFormat != null ? decimalFormat : (decimalFormat = new DecimalFormat("##.##"));
    }

    public TranslatableLocalization getLocalization() {
        return localization;
    }

    public HeadDBLogger getLog() {
        return logger;
    }

    public static HeadDB getInstance() {
        return instance;
    }

}
