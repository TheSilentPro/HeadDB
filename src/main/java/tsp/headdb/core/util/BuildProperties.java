package tsp.headdb.core.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This class contains the properties of the build.
 *
 * @author TheSilentPro (Silent)
 */
public class BuildProperties {

    private String version = "unknown";
    private int buildNumber = 0;
    private String revision = "unknown";
    private String branch = "unknown";
    private String timestamp = "unknown";
    private String author = "unknown";

    public BuildProperties(JavaPlugin plugin) {
        InputStream in = plugin.getResource("plugin.yml");
        if (in == null) {
            plugin.getLogger().severe("Could not get plugin.yml to read build information.");
            return;
        }

        YamlConfiguration data = YamlConfiguration.loadConfiguration(new InputStreamReader(in));
        this.version = data.getString("version", "unknown");
        this.buildNumber = data.getInt("build", 0);
        this.revision = data.getString("buildRevision", "unknown");
        this.branch = data.getString("buildBranch", "unknown");
        this.timestamp = data.getString("buildTimestamp", "unknown");
        this.author = data.getString("buildAuthor", "unknown");
    }

    public String getVersion() {
        return version;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public String getRevision() {
        return revision;
    }

    public String getBranch() {
        return branch;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getAuthor() {
        return author;
    }

}