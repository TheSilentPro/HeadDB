package tsp.headdb.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Utils {

    public static final int METRICS_ID = 9152;

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }

    public static String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY + string);
    }

}
