package tsp.headdb.util;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import tsp.headdb.HeadDB;

import java.util.regex.Pattern;

public class Utils {

    private static final FileConfiguration config = HeadDB.getInstance().getConfig();
    public static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");

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
