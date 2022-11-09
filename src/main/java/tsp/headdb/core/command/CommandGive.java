package tsp.headdb.core.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tsp.headdb.core.api.HeadAPI;
import tsp.headdb.core.util.Utils;
import tsp.headdb.implementation.head.Head;

import java.util.Optional;
import java.util.stream.Collectors;

public class CommandGive extends SubCommand {

    public CommandGive() {
        super("give", HeadAPI.getHeads().stream().map(Head::getName).collect(Collectors.toList()), "g");
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        // /hdb give <id> <player> [amount]
        if (args.length < 3) {
            getLocalization().sendMessage(sender, "invalidArguments");
            return;
        }

        int amount = args.length >= 4 ? Utils.resolveInt(args[3]) : 1;

        Optional<Head> head;
        String id = args[1];
        if (id.startsWith("t:")) {
            head = HeadAPI.getHeadByTexture(id.substring(2));
        } else {
            try {
                head = HeadAPI.getHeadById(Integer.parseInt(id));
            } catch (NumberFormatException nfe) {
                // Attempt to find it by exact name (useful for tab completions)
                head = HeadAPI.getHeadByExactName(id);
            }
        }

        Player player = Bukkit.getPlayer(args[2]);
        if (player == null) {
            getLocalization().sendMessage(sender, "invalidTarget", msg -> msg.replace("%name%", args[2]));
            return;
        }

        head.ifPresentOrElse(
                value -> {
                    player.getInventory().addItem(value.getItem(player.getUniqueId()));
                    getLocalization().sendMessage(sender, "giveCommand", msg -> msg.replace("%size%", String.valueOf(amount)).replace("%name%", value.getName()).replace("%receiver%", player.getName()));
                },
                () -> getLocalization().sendMessage(sender, "giveCommandInvalid", msg -> msg.replace("%name%", id))
        );
    }

}
