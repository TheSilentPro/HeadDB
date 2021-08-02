package tsp.headdb.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    public static boolean isValid(String uuid) {
        Matcher matcher = UUID_PATTERN.matcher(uuid);
        return matcher.find();
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }

    public static String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY + string);
    }

}
