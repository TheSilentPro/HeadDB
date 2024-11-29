package tsp.headdb.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.api.model.Head;
import tsp.headdb.core.util.Utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author TheSilentPro (Silent)
 */
public class CommandGive extends HDBCommand {

    public CommandGive() {
        super("give", "<player> <amount> <head>", "Give a specific head. You can use names, texture values, or prefix 'id:' for ids.", false, "g");
    }

    // hdb give <player> <amount> <head>
    @Override
    public void handle(@NotNull CommandSender sender, String[] args) {
        if (args.length < 3) {
            localization.sendMessage(sender, "invalidArguments");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            localization.sendMessage(sender, "invalidTarget", msg -> msg.replace("%name%", args[0]));
            return;
        }

        int amount = 1;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            localization.sendMessage(sender, "invalidNumber", msg -> msg.replace("%name%", args[1]));
        }

        //localization.sendMessage(sender, "");

        final int fAmount = amount;
        String id = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        HeadAPI.findHeadByExactName(id, true)
                .thenCompose(optionalHead -> {
                    if (optionalHead.isPresent()) {
                        return CompletableFuture.completedFuture(optionalHead.get());
                    } else if (id.startsWith("id:")) {
                        try {
                            int numericId = Integer.parseInt(id.substring(3));
                            return HeadAPI.findHeadById(numericId).thenApply(optional -> optional.orElse(null));
                        } catch (NumberFormatException e) {
                            return CompletableFuture.completedFuture(null);
                        }
                    } else {
                        return HeadAPI.findHeadByTexture(id).thenApply(optional -> optional.orElse(null));
                    }
                })
                .thenAcceptAsync(head -> {
                    if (head == null) {
                        localization.sendMessage(sender, "command.give.invalid", msg -> msg.replace("%name%", id));
                        return;
                    }
                    ItemStack item = head.getItem();
                    item.setAmount(fAmount);
                    target.getInventory().addItem(item);
                    localization.sendMessage(sender, "command.give.done", msg ->
                            msg.replace("%name%", head.getName())
                                    .replace("%amount%", String.valueOf(fAmount))
                                    .replace("%player%", target.getName()));
                }, Utils.SYNC);
    }

    private final List<String> one = Collections.singletonList("1"); // No reason to do more.

    @Override
    public List<String> handleCompletions(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        } else if (args.length == 2) {
            return one;
        } else if (args.length == 3) {
            if (sender instanceof ConsoleCommandSender) {
                return HeadAPI.getAllHeads().stream().map(Head::getName).toList().subList(1, 100); // The huge amount of heads can cause console terminals to crash.
            } else {
                return HeadAPI.getAllHeads().stream().map(Head::getName).toList();
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean waitUntilReady() {
        return true;
    }

}