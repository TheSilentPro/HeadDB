package tsp.headdb.api;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import tsp.headdb.HeadDB;
import tsp.headdb.api.model.Head;
import tsp.headdb.api.model.LocalHead;
import tsp.headdb.api.provider.HeadDataProvider;
import tsp.headdb.core.player.PlayerData;
import tsp.headdb.core.util.Utils;

import org.jetbrains.annotations.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Head API for interacting with the main {@link HeadDatabase}.
 *
 * @author TheSilentPro (Silent)
 * @see HeadDatabase
 */
public final class HeadAPI {

    /**
     * Utility class. No initialization nor extension.
     */
    private HeadAPI() {}

    static final ExecutorService executor = Executors.newFixedThreadPool(2, r -> new Thread(r, "HeadDB Fetcher"));

    /**
     * The main {@link HeadDatabase}.
     */
    private static final HeadDatabase database = new HeadDatabase(HeadDB.getInstance(), executor, new HeadDataProvider());

    /**
     * Retrieve a {@link List} of {@link Head} matching the name.
     *
     * @param name The name to match against
     * @param lenient Whether the filter should be lenient when matching
     * @return {@link List<Head> Heads}
     */
    @NotNull
    public static CompletableFuture<List<Head>> getHeadsByName(String name, boolean lenient) {
        return CompletableFuture.supplyAsync(() -> getHeadStream().filter(head -> (lenient ? Utils.matches(head.getName(), name) : head.getName().equalsIgnoreCase(name))).collect(Collectors.toList()), executor);
    }

    /**
     * Retrieve a {@link List} of {@link Head} matching the name.
     *
     * @param name The name to match against
     * @return {@link List<Head> Heads}
     */
    @NotNull
    public static CompletableFuture<List<Head>> getHeadsByName(String name) {
        return getHeadsByName(name, true);
    }

    /**
     * Retrieve a {@link Head} by its exact name.
     *
     * @param name The name to look for
     * @param lenient Whether the filter to be lenient when matching
     * @return The {@link Head}, else empty
     */
    public static CompletableFuture<Optional<Head>> getHeadByExactName(String name, boolean lenient) {
        return CompletableFuture.supplyAsync(getHeadStream().filter(head -> (lenient ? Utils.matches(head.getName(), name) : head.getName().equalsIgnoreCase(name)))::findAny, executor);
    }

    /**
     * Retrieve a {@link Head} by its exact name.
     *
     * @param name The name to look for
     * @return The {@link Head}, else empty
     */
    @NotNull
    public static CompletableFuture<Optional<Head>> getHeadByExactName(String name) {
        return getHeadByExactName(name, false);
    }

    /**
     * Retrieve a {@link Head} by its id.
     *
     * @param id The id to look for
     * @return The {@link Head}, else empty
     */
    @NotNull
    public static CompletableFuture<Optional<Head>> getHeadById(int id) {
        return CompletableFuture.supplyAsync(getHeadStream().filter(head -> head.getId() == id)::findAny, executor);
    }

    /**
     * Retrieve a {@link Head} by its texture value.
     *
     * @param texture The texture to look for
     * @return The {@link Head}, else empty
     */
    @NotNull
    public static CompletableFuture<Optional<Head>> getHeadByTexture(String texture) {
        return CompletableFuture.supplyAsync(getHeadStream().filter(head -> head.getTexture().orElse("N/A").equals(texture))::findAny, executor);
    }

    /**
     * Retrieve a {@link List} of {@link Head Heads} matching the category.
     *
     * @param category The category
     * @return The list of matching heads.
     */
    @NotNull
    public static CompletableFuture<List<Head>> getHeadsByCategory(String category) {
        return CompletableFuture.supplyAsync(() -> getHeadStream().filter(head -> head.getCategory().orElse("?").equalsIgnoreCase(category)).toList(), executor);
    }

    /**
     * Retrieve a {@link List} of {@link Head Heads} matching the date.
     *
     * @param date The date
     * @return The list of matching heads.
     */
    @NotNull
    public static CompletableFuture<List<Head>> getHeadsByDate(String date) {
        return CompletableFuture.supplyAsync(() -> getHeadStream().filter(head -> head.getPublishDate().orElse("?").equalsIgnoreCase(date)).toList(), executor);
    }

    /**
     * Retrieve a {@link List} of {@link Head Heads} matching the date.
     *
     * @param dates The dates
     * @return The list of matching heads.
     */
    @NotNull
    public static CompletableFuture<List<Head>> getHeadsByDates(String... dates) {
        return CompletableFuture.supplyAsync(() -> getHeadStream().filter(head -> {
            if (head.getPublishDate().isEmpty()) {
                return false;
            }
            for (String date : dates) {
                if (head.getPublishDate().get().equalsIgnoreCase(date)) {
                    return true;
                }
            }
            return false;
        }).toList(), executor);
    }

    /**
     * Retrieve a {@link List} of {@link Head Heads} matching the tags.
     *
     * @param tags The tags
     * @return The list of matching heads.
     */
    @NotNull
    public static CompletableFuture<List<Head>> getHeadsByTags(String... tags) {
        return CompletableFuture.supplyAsync(() -> getHeadStream().filter(head -> {
            String[] array = head.getTags().orElse(null);
            if (array == null) {
                return false;
            }
            for (String entry : array) {
                for (String tag : tags) {
                    if (entry.equalsIgnoreCase(tag)) {
                        return true;
                    }
                }
            }
            return false;
        }).toList(), executor);
    }

    /**
     * Retrieve a {@link List} of {@link Head Heads} matching the contributors.
     *
     * @param contributors The contributors
     * @return The list of matching heads.
     */
    @NotNull
    public static CompletableFuture<List<Head>> getHeadsByContributors(String... contributors) {
        return CompletableFuture.supplyAsync(() -> getHeadStream().filter(head -> {
            String[] array = head.getContributors().orElse(null);
            if (array == null) {
                return false;
            }
            for (String entry : array) {
                for (String contributor : contributors) {
                    if (entry.equalsIgnoreCase(contributor)) {
                        return true;
                    }
                }
            }
            return false;
        }).toList(), executor);
    }

    /**
     * Retrieve a {@link List} of {@link Head Heads} matching the collections.
     *
     * @param collections The collections
     * @return The list of matching heads.
     */
    @NotNull
    public static CompletableFuture<List<Head>> getHeadsByCollections(String... collections) {
        return CompletableFuture.supplyAsync(() -> getHeadStream().filter(head -> {
            String[] array = head.getTags().orElse(null);
            if (array == null) {
                return false;
            }
            for (String entry : array) {
                for (String collection : collections) {
                    if (entry.equalsIgnoreCase(collection)) {
                        return true;
                    }
                }
            }
            return false;
        }).toList(), executor);
    }

    /**
     * Retrieve a {@link Stream} of {@link Head} within the main {@link HeadDatabase}.
     * 
     * @return The streamed heads
     */
    @NotNull
    public static Stream<Head> getHeadStream() {
        return getHeads().stream();
    }

    /**
     * Retrieve a {@link List} of {@link Head} within the main {@link HeadDatabase}.
     *
     * @return {@link List<Head> Heads}
     */
    @NotNull
    public static List<Head> getHeads() {
        return database.getHeads();
    }

    /**
     * Retrieve the total amount of {@link Head heads} present in the main {@link HeadDatabase}.
     *
     * @return Amount of heads
     */
    public static int getTotalHeads() {
        return getHeads().size();
    }

    /**
     * Retrieve a {@link Set} of local heads.
     * Note that this calculates the heads on every call.
     *
     * @return {@link Set<LocalHead> Local Heads}
     */
    @NotNull
    public static CompletableFuture<Set<LocalHead>> getLocalHeads(boolean calculateDate) {
        OfflinePlayer[] players = Bukkit.getOfflinePlayers();
        return CompletableFuture.supplyAsync(() -> Arrays.stream(players)
                .filter(player -> player.getName() != null)
                .map(player -> {
                    PlayerData data = HeadDB.getInstance().getPlayerDatabase().getOrCreate(player.getUniqueId());
                    String date = null;
                    if (calculateDate) {
                        date = Instant.ofEpochMilli(player.getFirstPlayed())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .format(Utils.DATE_FORMATTER);
                    }
                    return new LocalHead(data.getId(), data.getUuid(), player.getName(), date);
                })
                .collect(Collectors.toSet()), executor);
    }

    public static CompletableFuture<Set<LocalHead>> getLocalHeads() {
        return getLocalHeads(false);
    }

    /**
     * Retrieve a {@link Set} of favorite heads for the specified {@link UUID player id}.
     * Note that this calculates the heads on every call.
     *
     * @return {@link Set<Head> Favorite Heads}
     */
    @NotNull
    public static CompletableFuture<Set<Head>> getFavoriteHeads(UUID player) {
        return CompletableFuture.supplyAsync(() -> HeadDB.getInstance()
                .getPlayerDatabase()
                .getOrCreate(player)
                .getFavorites()
                .stream()
                .map(id -> {
                    Optional<Head> head = HeadAPI.getHeadById(id).join();
                    if (head.isPresent()) {
                        return head;
                    } else {
                        return getLocalHeads()
                                .join()
                                .stream()
                                .filter(localHead -> localHead.getId() == id)
                                .findAny();
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet()), executor);
    }

    /**
     * Returns true when the database is ready with all heads cached.
     *
     * @return If the database has cached all heads.
     */
    public boolean isReady() {
        return database.isReady();
    }

    /**
     * Retrieve the main {@link HeadDatabase} used by the plugin.
     *
     * @return {@link HeadDatabase Database}
     */
    @NotNull
    public static HeadDatabase getDatabase() {
        return database;
    }

}