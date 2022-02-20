package tsp.headdb.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tsp.headdb.HeadDB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class Utils {

    private static final FileConfiguration config = HeadDB.getInstance().getConfig();
    public static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");

    /**
     * Retrieve the latest release from spigot
     *
     * @param plugin The plugin
     * @param id The resource id on spigot
     * @param latest Whether the plugin is on the latest version
     */
    public static void isLatestVersion(JavaPlugin plugin, int id, Consumer<Boolean> latest) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, checkTask -> {
            try {
                URLConnection connection = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + id).openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestProperty("User-Agent", plugin.getName() + "-VersionChecker");

                latest.accept(new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine().equals(plugin.getDescription().getVersion()));
            } catch (IOException e) {
                latest.accept(true); // Assume the version is latest if checking fails
                e.printStackTrace();
            }
        });
    }

    /**
     * Validate a UUID (version 4)
     *
     * @param uuid UUID to be validated
     * @return Returns true if the string is a valid UUID
     */
    public static boolean validateUniqueId(String uuid) {
        return UUID_PATTERN.matcher(uuid).matches();
    }

    public static void playSound(Player player, String key) {
        // Check if sound is enabled
        if (!config.getBoolean("ui.sound.enabled")) {
            return;
        }

        player.playSound(player.getLocation(),
                Sound.valueOf(config.getString("ui.sound." + key + ".name")),
                (float) config.getDouble("ui.sound." + key + ".volume"),
                (float) config.getDouble("ui.sound." + key + ".pitch"));
    }

    public static void sendMessage(CommandSender sender, String message) {
        if (!message.isEmpty()) {
            sender.sendMessage(colorize(message));
        }
    }

    public static String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
