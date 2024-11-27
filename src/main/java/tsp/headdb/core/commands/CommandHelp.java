package tsp.headdb.core.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import tsp.headdb.HeadDB;

/**
 * @author TheSilentPro (Silent)
 */
public class CommandHelp extends HDBCommand {

    private TextComponent[] messages;

    public CommandHelp() {
        super("help", "Shows the help message.", false, "h", "commands", "cmds");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (messages == null) {
            int size = HeadDB.getInstance().getCommandManager().getCommands().size();
            messages = new TextComponent[size];
            int index = 0;
            for (HDBCommand command : HeadDB.getInstance().getCommandManager().getCommands()) {
                TextComponent component = new TextComponent(ChatColor.GRAY + "/hdb " + ChatColor.DARK_AQUA + command.getName() + " " + ChatColor.DARK_AQUA + command.getParameters() + ChatColor.GRAY + " - " + command.getDescription());
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new Text(ChatColor.GRAY + "Aliases: " + ChatColor.GOLD + String.join(ChatColor.GRAY + " | " + ChatColor.GOLD, command.getAliases())),
                        new Text(ChatColor.GRAY + "\nPermission: " + ChatColor.RED + "headdb.command." + command.getName()),
                        new Text("\n"),
                        new Text(command.isNoConsole() ? ChatColor.RED + "\nThis command CAN NOT be used from console!" : ChatColor.GREEN + "\nThis command CAN be used from console!")
                ));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/hdb " + command.getName() + " "));
                messages[index] = component;
                index++;
            }
        }

        sender.sendMessage(ChatColor.GRAY + "<==================== [ " + ChatColor.RED + "HeadDB " + ChatColor.GRAY + "|" + ChatColor.DARK_PURPLE + "Commands" + ChatColor.GRAY + "] ====================>");
        sender.sendMessage(ChatColor.GRAY + "Format: /hdb " + ChatColor.DARK_AQUA + "<sub-command> " + ChatColor.RED + "<parameters> " + ChatColor.GRAY + "- Description");
        sender.sendMessage(ChatColor.GRAY + "Required: " + ChatColor.RED + "<> " + ChatColor.GRAY + "| Optional: " + ChatColor.AQUA + "[]");
        sender.sendMessage(" ");
        for (TextComponent message : messages) {
            sender.spigot().sendMessage(message);
        }
        sender.sendMessage(ChatColor.GRAY + "<===============================================================>");
    }

}