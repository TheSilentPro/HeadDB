package tsp.headdb.core.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.api.model.Head;
import tsp.headdb.core.util.MenuSetup;
import tsp.headdb.core.util.Utils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author TheSilentPro (Silent)
 */
public class CommandSearch extends HDBCommand {

    public CommandSearch() {
        super("search", "[tags:|contributors:|collections:|dates:|before:|after:] <query>", "Search the database with possible filters.", true, "s", "find");
    }

    // hdb search <search>
    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length < 1) {
            localization.sendMessage(sender, "invalidArguments");
            return;
        }

        String id = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        localization.sendMessage(sender, "command.search.wait", msg -> msg.replace("%name%", id));

        if (id.startsWith("tags:")) {
            args[0] = args[0].substring(5); // Remove prefix
            HeadAPI.getHeadsByTags(args).thenAcceptAsync(heads -> handle(heads, sender, id.substring(5)), Utils.SYNC);
            return;
        }

        if (id.startsWith("contributors:")) {
            args[0] = args[0].substring(13); // Remove prefix
            HeadAPI.getHeadsByContributors(args).thenAcceptAsync(heads -> handle(heads, sender, id.substring(13)), Utils.SYNC);
            return;
        }

        if (id.startsWith("collections:")) {
            args[0] = args[0].substring(12); // Remove prefix
            HeadAPI.getHeadsByCollections(args).thenAcceptAsync(heads -> handle(heads, sender, id.substring(12)), Utils.SYNC);
            return;
        }

        if (id.startsWith("dates:")) {
            args[0] = args[0].substring(6); // Remove prefix
            HeadAPI.getHeadsByDates(args).thenAcceptAsync(heads -> handle(heads, sender, id.substring(6)), Utils.SYNC);
            return;
        }

        if (id.startsWith("before:")) {
            LocalDate date = LocalDate.parse(id.substring(7), Utils.DATE_FORMATTER);
            CompletableFuture.supplyAsync(() -> HeadAPI.getHeadStream().filter(head -> {
                if (head.getPublishDate().isEmpty()) {
                    return false;
                }
                return LocalDate.parse(head.getPublishDate().get(), Utils.DATE_FORMATTER).isBefore(date);
            }).toList()).thenAcceptAsync(heads -> handle(heads, sender, id.substring(7)), Utils.SYNC);
            return;
        }

        if (id.startsWith("after:")) {
            LocalDate date = LocalDate.parse(id.substring(6), Utils.DATE_FORMATTER);
            CompletableFuture.supplyAsync(() -> HeadAPI.getHeadStream().filter(head -> {
                if (head.getPublishDate().isEmpty()) {
                    return false;
                }
                return LocalDate.parse(head.getPublishDate().get(), Utils.DATE_FORMATTER).isAfter(date);
            }).toList()).thenAcceptAsync(heads -> handle(heads, sender, id.substring(6)), Utils.SYNC);
            return;
        }

        HeadAPI.getHeadsByName(id, true).thenAcceptAsync(heads -> handle(heads, sender, id), Utils.SYNC);
    }

    private void handle(List<Head> heads, CommandSender sender, String id) {
        if (heads.isEmpty()) {
            localization.sendMessage(sender, "command.search.invalid", msg -> msg.replace("%name%", id));
            return;
        }

        localization.sendMessage(sender, "command.search.done", msg -> msg.replace("%name%", id).replace("%size%", String.valueOf(heads.size())));
        MenuSetup.openSearch(heads, id, (Player) sender);
    }

    @Override
    public boolean waitUntilReady() {
        return true;
    }

}