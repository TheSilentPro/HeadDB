package tsp.headdb;

import tsp.headdb.core.command.CommandMain;
import tsp.headdb.core.command.CommandManager;
import tsp.headdb.core.task.DatabaseUpdateTask;
import tsp.smartplugin.SmartPlugin;
import tsp.smartplugin.inventory.PaneListener;
import tsp.smartplugin.localization.TranslatableLocalization;
import tsp.smartplugin.logger.AbstractLogger;
import tsp.smartplugin.logger.PluginLogger;

import java.io.IOException;
import java.net.URISyntaxException;

public class HeadDB extends SmartPlugin {

    private static final HeadDB instance = new HeadDB();
    private AbstractLogger logger;
    private TranslatableLocalization localization;
    private CommandManager commandManager;

    @Override
    public void onStart() {
        instance.logger = new PluginLogger(this, getConfig().getBoolean("debug"));
        instance.logger.info("Loading HeadDB - " + instance.getDescription().getVersion());
        instance.saveDefaultConfig();

        new PaneListener(this);

        new DatabaseUpdateTask(getConfig().getLong("refresh"));

        instance.logger.info("Loaded " + loadLocalization() + " languages!");

        //noinspection ConstantConditions
        instance.getCommand("headdb").setExecutor(new CommandMain());

        instance.logger.info("Done!");
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

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public TranslatableLocalization getLocalization() {
        return localization;
    }

    public AbstractLogger getLog() {
        return logger;
    }

    public static HeadDB getInstance() {
        return instance;
    }

}
