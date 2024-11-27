package tsp.headdb.core.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import tsp.headdb.HeadDB;
import tsp.headdb.core.util.Utils;

/**
 * @author TheSilentPro (Silent)
 */
public class CommandInfo extends HDBCommand {

    public CommandInfo() {
        super("info", "Show information about the plugin.", false, "i");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        TextComponent component = new TextComponent(ChatColor.GRAY + "Running " + ChatColor.GOLD + "HeadDB - " + HeadDB.getInstance().getDescription().getVersion() + "\n" + ChatColor.GRAY + "GitHub: " + ChatColor.GOLD + "https://github.com/TheSilentPro/HeadDB");
        if (sender.hasPermission("headdb.admin")) {
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GOLD + "CLICK TO COPY INFO: " + Utils.getUserAgent())));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, Utils.getUserAgent()));
        }
        sender.spigot().sendMessage(component);
    }

}