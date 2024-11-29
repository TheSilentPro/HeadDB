package tsp.headdb.core.commands;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.api.model.Head;
import tsp.headdb.core.util.MenuSetup;
import tsp.headdb.core.util.Utils;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author TheSilentPro (Silent)
 */
public class CommandSearch extends HDBCommand {

    // Compile the patterns for predefined filters
    private final Pattern FILTERS_PATTERN = Pattern.compile("(?<=\\s|^)(tags|contributors|collections|dates|before|after):([^:\\s]+(?:\\s*[^:\\s]+)*)(?=\\s|$)");

    // Pattern to match only the first 'head:' or 'query:' followed by the query value
    private final Pattern QUERY_PATTERN = Pattern.compile("(?<=\\s|^)(head:|query:)([^\\s]+(?:\\s+[^:\\s]+)*)");
    private final List<String> completions = List.of(
            "tags:",
            "contributors:",
            "collections:",
            "dates:",
            "before:",
            "after:",
            "query:"
    );

    public CommandSearch() {
        super("search", "[tags:|contributors:|collections:|dates:|before:|after:] <query:>", "Search the database with possible filters.", true, "s", "find");
    }

    // TODO: Optimize?
    // TODO: Make final results match ALL filters AND queries.
    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length < 1) {
            localization.sendMessage(sender, "invalidArguments");
            return;
        }

        final String input = String.join(" ", Arrays.copyOfRange(args, 0, args.length));

        // Create matchers for the input string
        final Matcher filtersMatcher = FILTERS_PATTERN.matcher(input);
        final Matcher queryMatcher = QUERY_PATTERN.matcher(input);

        // Store results for filters
        final Map<String, Set<String>> filters = new HashMap<>();

        // Process predefined filters
        while (filtersMatcher.find()) {
            final String filter = filtersMatcher.group(1);  // Filter name (tags, contributors, etc.)
            final String values = filtersMatcher.group(2);  // Values associated with the filter

            // Split the values by commas instead of spaces
            final String[] valueArray = values.split("\\s*,\\s*");
            filters.put(filter, Set.of(valueArray));
        }

        // Determine the query
        String query = null;
        if (!filters.isEmpty()) {
            // If filters exist, search for the query (head: or query:)
            if (queryMatcher.find()) {
                query = queryMatcher.group(2);  // Capture the query after 'head:' or 'query:'
            }

            String msg = localization.getMessage(((Player) sender).getUniqueId(), "command.search.wait").orElse(null);
            if (msg != null) {
                sender.spigot().sendMessage(withText(Utils.colorize(msg.replace("%name%", query != null ? query : "matching heads")), filters));
            }
        } else {
            localization.sendMessage(sender, "command.search.wait", msg -> msg.replace("%name%", input));
            HeadAPI.findHeadsByName(input, true).thenAcceptAsync(heads -> {
                if (heads.isEmpty()) {
                    localization.sendMessage(sender, "command.search.invalid", msg -> msg.replace("%name%", input));
                    return;
                }

                localization.sendMessage(sender, "command.search.done", msg -> msg.replace("%name%", input).replace("%size%", String.valueOf(heads.size())));
                MenuSetup.openSearch(heads, input, (Player) sender);
            }, Utils.SYNC);
            return;
        }

        final List<Head> result = new ArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // Process filters and retrieve results accordingly
        for (Map.Entry<String, Set<String>> entry : filters.entrySet()) {
            final String filter = entry.getKey();
            final Set<String> values = entry.getValue();

            CompletableFuture<Void> future = null;

            if (filter.equalsIgnoreCase("tags")) {
                future = HeadAPI.findHeadsByTags(values.toArray(new String[0]))
                        .thenAccept(result::addAll);
            } else if (filter.equalsIgnoreCase("contributors")) {
                future = HeadAPI.findHeadsByContributors(values.toArray(new String[0]))
                        .thenAccept(result::addAll);
            } else if (filter.equalsIgnoreCase("collections")) {
                future = HeadAPI.findHeadsByCollections(values.toArray(new String[0]))
                        .thenAccept(result::addAll);
            } else if (filter.equalsIgnoreCase("dates")) {
                future = HeadAPI.findHeadsByDates(values.toArray(new String[0]))
                        .thenAccept(result::addAll);
            } else if (filter.equalsIgnoreCase("before")) {
                final LocalDate date = LocalDate.parse(values.iterator().next(), Utils.DATE_FORMATTER);
                future = CompletableFuture.supplyAsync(() -> HeadAPI.streamHeads().filter(head -> {
                    if (head.getPublishDate().isEmpty()) {
                        return false;
                    }
                    return LocalDate.parse(head.getPublishDate().get(), Utils.DATE_FORMATTER).isBefore(date);
                }).toList()).thenAccept(result::addAll);
            } else if (filter.equalsIgnoreCase("after")) {
                final LocalDate date = LocalDate.parse(values.iterator().next(), Utils.DATE_FORMATTER);
                future = CompletableFuture.supplyAsync(() -> HeadAPI.streamHeads().filter(head -> {
                    if (head.getPublishDate().isEmpty()) {
                        return false;
                    }
                    return LocalDate.parse(head.getPublishDate().get(), Utils.DATE_FORMATTER).isAfter(date);
                }).toList()).thenAccept(result::addAll);
            }

            if (future != null) {
                futures.add(future);
            }
        }

        final String fquery = query;

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRunAsync(() -> {
                    // Filter the results based on the query (if any)
                    final Set<Head> heads;
                    if (fquery != null) {
                        // If query is not null, match it against head names or other fields
                        heads = result.stream().filter(head -> Utils.matches(head.getName(), fquery) || Arrays.asList(head.getTags().orElse(new String[0])).contains(fquery)).collect(Collectors.toSet());
                    } else {
                        // If no query, return results only filtered by the tags
                        heads = new HashSet<>(result);
                    }

                    if (heads.isEmpty()) {
                        localization.sendMessage(sender, "command.search.invalid", msg -> msg.replace("%name%", input));
                        return;
                    }

                    localization.sendMessage(sender, "command.search.done", msg -> msg.replace("%name%", input).replace("%size%", String.valueOf(heads.size())));
                    MenuSetup.openSearch(new ArrayList<>(heads), fquery, (Player) sender);
                }, Utils.SYNC);
    }

    @Override
    public boolean waitUntilReady() {
        return true;
    }

    @Override
    public List<String> handleCompletions(CommandSender sender, String[] args) {
        return completions;
    }

    private TextComponent withText(String text, Map<String, Set<String>> filters) {
        TextComponent component = new TextComponent(Utils.colorize(text));
        StringBuilder builder = new StringBuilder(ChatColor.GRAY + "Filters:");
        for (Map.Entry<String, Set<String>> filter : filters.entrySet()) {
            builder.append("\n").append(ChatColor.GRAY).append(filter.getKey()).append(": ").append(ChatColor.GOLD).append(String.join(",", filter.getValue()));
        }
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(builder.toString())));
        return component;
    }

}