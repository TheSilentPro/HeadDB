package tsp.headdb.core.command;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tsp.headdb.core.util.Utils;
import tsp.smartplugin.utils.StringUtils;

public class CommandTexture extends SubCommand {

    public CommandTexture() {
        super("texture", "t");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            getLocalization().sendConsoleMessage("noConsole");
            return;
        }

        Utils.getTexture(player.getInventory().getItemInMainHand()).ifPresentOrElse(texture -> getLocalization().getMessage(player.getUniqueId(), "itemTexture").ifPresent(message -> {
            TextComponent component = new TextComponent(StringUtils.colorize(message.replace("%texture%", texture)));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(StringUtils.colorize(getLocalization().getMessage(player.getUniqueId(), "copyTexture").orElse("Click to copy!")))));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, texture));
            player.spigot().sendMessage(component);
        }), () -> getLocalization().sendMessage(sender,"itemNoTexture"));
    }

}
