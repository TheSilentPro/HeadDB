package tsp.headdb.util;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

/**
 * Localization class for a messages file
 *
 * @author TheSilentPro
 */
public class Localization {

    private final File file;
    @Nullable
    private FileConfiguration data;

    public Localization(@Nonnull File file) {
        Validate.notNull(file, "File can not be null.");

        this.file = file;
        this.data = null;
    }

    public String getMessage(@Nonnull String key) {
        Validate.notNull(data, "File data is null.");
        Validate.notNull(key, "Message key can not be null.");

        return Utils.colorize(data.getString(key, "null"));
    }

    public void load() {
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    public File getFile() {
        return file;
    }

    @Nullable
    public FileConfiguration getData() {
        return data;
    }

}
