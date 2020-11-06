package tsp.headdb.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author TheSilentPro
 */
public class Config {

    private final File file;
    private FileConfiguration config;

    public Config(File file) {
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public Config(String path) {
        this.file = new File(path);
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public Config(Config config) {
        this.file = config.getFile();
        this.config = config.getConfig();
    }

    public String getString(String path) {
        return Utils.colorize(config.getString(path));
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public List<Integer> getIntList(String path) {
        return config.getIntegerList(path);
    }

    public long getLong(String path) {
        return config.getLong(path);
    }

    public Set<String> getKeys(boolean deep) {
        return config.getKeys(deep);
    }

    public ConfigurationSection getConfigurationSection(String path) {
        return config.getConfigurationSection(path);
    }

    public Set<String> getKeys(ConfigurationSection configurationSection, boolean b) {
        return configurationSection.getKeys(b);
    }

    public void set(String path, Object value) {
        config.set(path, value);
    }

    public Object get(String path) {
        return config.get(path);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public File getFile() {
        return file;
    }

    public boolean exists() {
        return file.exists();
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean create() {
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean save() {
        try {
            config.save(file);
            return true;
        } catch (IOException e) {
            Log.error("Failed to save " + file.getName() + " | Stack Trace:");
            e.printStackTrace();
        }

        return false;
    }

}
