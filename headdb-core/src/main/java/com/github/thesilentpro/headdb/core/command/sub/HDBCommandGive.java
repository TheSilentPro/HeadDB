package com.github.thesilentpro.headdb.core.command.sub;

import com.github.thesilentpro.headdb.api.model.Head;
import com.github.thesilentpro.headdb.core.HeadDB;
import com.github.thesilentpro.headdb.core.command.HDBSubCommand;
import com.github.thesilentpro.headdb.core.util.Compatibility;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class HDBCommandGive extends HDBSubCommand {

    private final HeadDB plugin;

    public HDBCommandGive(HeadDB plugin) {
        super("give", "Give a specific head to a player.", "<player> <amount> <head>", "g");
        this.plugin = plugin;
    }

    // /hdb give <player> <amount> <head>
    @Override
    public void handle(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            plugin.getLocalization().sendMessage(sender, "invalidTarget", msg -> msg.replaceText(builder -> builder.matchLiteral("{target}").replacement(args[1])));
            return;
        }

        int amount = 1;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException nfe) {
            plugin.getLocalization().sendMessage(sender, "invalidNumber", msg -> msg.replaceText(builder -> builder.matchLiteral("{number}").replacement(args[2])));
        }

        final int fAmount = amount;
        String id = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        plugin.getHeadApi().findByName(id, true)
                .thenCompose(optionalHead -> {
                    if (optionalHead.isPresent()) {
                        return CompletableFuture.completedFuture(optionalHead.get());
                    } else if (id.startsWith("id:")) {
                        try {
                            int numericId = Integer.parseInt(id.substring(3));
                            return plugin.getHeadApi().findById(numericId).thenApply(optional -> optional.orElse(null));
                        } catch (NumberFormatException e) {
                            return CompletableFuture.completedFuture(null);
                        }
                    } else {
                        return plugin.getHeadApi().findByTexture(id).thenApply(optional -> optional.orElse(null));
                    }
                })
                .thenAcceptAsync(head -> {
                    if (head == null) {
                        plugin.getLocalization().sendMessage(sender, "command.give.invalidId", msg -> msg.replaceText(builder -> builder.matchLiteral("{id}").replacement(id)));
                        return;
                    }
                    ItemStack item = Compatibility.setItemDetails(head.getItem(), Component.text(head.getName()));
                    item.setAmount(fAmount);
                    target.getInventory().addItem(item);
                    plugin.getLocalization().sendMessage(sender, "command.give.success", msg ->
                            msg.replaceText(builder -> builder.matchLiteral("{amount}").replacement(String.valueOf(fAmount)))
                            .replaceText(builder -> builder.matchLiteral("{name}").replacement(head.getName()))
                            .replaceText(builder -> builder.matchLiteral("{target}").replacement(target.getName()))
                    );
                }, Compatibility.getMainThreadExecutor(plugin));
    }

    private static final List<String> numberCompletions = List.of("1", "32", "64");

    @Override
    public @Nullable List<String> handleCompletions(CommandSender sender, String[] args) {
        if (args.length == 3) {
            return numberCompletions;
        }

        if (args.length >= 4) {
            String prefix = String.join(" ", Arrays.copyOfRange(args, 3, args.length)).trim().toLowerCase(Locale.ROOT);

            Stream<String> heads = plugin.getHeadApi().getHeads().join().stream().map(Head::getName);

            if (prefix.isEmpty()) {
                return heads.toList();
            }

            return heads.filter(name -> name.toLowerCase(Locale.ROOT).startsWith(prefix)).toList();
        }

        return null;
    }

}
