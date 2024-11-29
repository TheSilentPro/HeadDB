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
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
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

    private static final AtomicInteger threadId = new AtomicInteger(1);
    private static final ExecutorService executor = Utils.from(HeadDB.getInstance().getCfg().getDatabaseWorkerThreads(), "HeadDB Database Worker | " + threadId.getAndIncrement());

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
    public static CompletableFuture<List<Head>> findHeadsByName(String name, boolean lenient) {
        return CompletableFuture.supplyAsync(() -> streamHeads().filter(head -> (lenient ? Utils.matches(head.getName(), name) : head.getName().equalsIgnoreCase(name))).collect(Collectors.toList()), executor);
    }

    /**
     * Retrieve a {@link List} of {@link Head} matching the name.
     *
     * @param name The name to match against
     * @return {@link List<Head> Heads}
     */
    @NotNull
    public static CompletableFuture<List<Head>> findHeadsByName(String name) {
        return findHeadsByName(name, true);
    }

    /**
     * Retrieve a {@link Head} by its exact name.
     *
     * @param name The name to look for
     * @param lenient Whether the filter to be lenient when matching
     * @return The {@link Head}, else empty
     */
    public static CompletableFuture<Optional<Head>> findHeadByExactName(String name, boolean lenient) {
        return CompletableFuture.supplyAsync(streamHeads().filter(head -> (lenient ? Utils.matches(head.getName(), name) : head.getName().equalsIgnoreCase(name)))::findAny, executor);
    }

    /**
     * Retrieve a {@link Head} by its exact name.
     *
     * @param name The name to look for
     * @return The {@link Head}, else empty
     */
    @NotNull
    public static CompletableFuture<Optional<Head>> findHeadByExactName(String name) {
        return findHeadByExactName(name, false);
    }

    /**
     * Retrieve a {@link Head} by its id.
     *
     * @param id The id to look for
     * @return The {@link Head}, else empty
     */
    @NotNull
    public static CompletableFuture<Optional<Head>> findHeadById(int id) {
        return CompletableFuture.supplyAsync(streamHeads().filter(head -> head.getId() == id)::findAny, executor);
    }

    /**
     * Retrieve a {@link Head} by its texture value.
     *
     * @param texture The texture to look for
     * @return The {@link Head}, else empty
     */
    @NotNull
    public static CompletableFuture<Optional<Head>> findHeadByTexture(String texture) {
        return CompletableFuture.supplyAsync(streamHeads().filter(head -> head.getTexture().orElse("N/A").equals(texture))::findAny, executor);
    }

    /**
     * Retrieve a {@link List} of {@link Head Heads} matching the category.
     *
     * @param category The category
     * @return The list of matching heads.
     */
    @NotNull
    public static CompletableFuture<List<Head>> findHeadsByCategory(String category) {
        return CompletableFuture.supplyAsync(() -> streamHeads().filter(head -> head.getCategory().orElse("?").equalsIgnoreCase(category)).toList(), executor);
    }

    /**
     * Retrieve a {@link List} of {@link Head Heads} matching the date.
     *
     * @param date The date
     * @return The list of matching heads.
     */
    @NotNull
    public static CompletableFuture<List<Head>> findHeadsByDate(String date) {
        return CompletableFuture.supplyAsync(() -> streamHeads().filter(head -> head.getPublishDate().orElse("?").equalsIgnoreCase(date)).toList(), executor);
    }

    /**
     * Retrieve a {@link List} of {@link Head Heads} matching the date.
     *
     * @param dates The dates
     * @return The list of matching heads.
     */
    @NotNull
    public static CompletableFuture<List<Head>> findHeadsByDates(String... dates) {
        return CompletableFuture.supplyAsync(() -> streamHeads().filter(head -> {
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
    public static CompletableFuture<List<Head>> findHeadsByTags(String... tags) {
        return CompletableFuture.supplyAsync(() -> streamHeads().filter(head -> {
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
    public static CompletableFuture<List<Head>> findHeadsByContributors(String... contributors) {
        return CompletableFuture.supplyAsync(() -> streamHeads().filter(head -> {
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
    public static CompletableFuture<List<Head>> findHeadsByCollections(String... collections) {
        return CompletableFuture.supplyAsync(() -> streamHeads().filter(head -> {
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
     * Retrieve a {@link Set} of local heads.
     * Note that this calculates the heads on every call.
     *
     * @param calculateDate Calculate date joined
     * @return {@link Set<LocalHead> Local Heads}
     */
    @NotNull
    public static CompletableFuture<Set<LocalHead>> computeLocalHeads(boolean calculateDate) {
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

    /**
     * Retrieve a {@link Set} of local heads.
     *
     * @return {@link Set} of local heads.
     */
    @NotNull
    public static CompletableFuture<Set<LocalHead>> computeLocalHeads() {
        return computeLocalHeads(false);
    }

    /**
     * Retrieve a {@link Set} of local heads.
     * Note that this calculates the heads on every call.
     *
     * @param player The player uuid
     * @param calculateDate Calculate date joined
     * @return {@link Set<LocalHead> Local Heads}
     */
    @NotNull
    public static CompletableFuture<Optional<LocalHead>> computeLocalHead(UUID player, boolean calculateDate) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(player);
        if (p.getName() == null) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return CompletableFuture.supplyAsync(() -> {
            PlayerData data = HeadDB.getInstance().getPlayerDatabase().getOrCreate(player);
            String date = null;
            if (calculateDate) {
                date = Instant.ofEpochMilli(p.getFirstPlayed())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(Utils.DATE_FORMATTER);
            }
            return Optional.of(new LocalHead(data.getId(), data.getUuid(), p.getName(), date));
        });
    }

    /**
     * Retrieve a {@link Set} of favorite heads for the specified {@link UUID player id}.
     * Note that this calculates the heads on every call.
     *
     * @param player The player uuid
     * @param calculateDate Calculate date joined
     * @return {@link Set<Head> Favorite Heads}
     */
    @NotNull
    public static CompletableFuture<Set<Head>> findFavoriteHeads(UUID player, boolean calculateDate) {
        return CompletableFuture.supplyAsync(() -> HeadDB.getInstance()
                .getPlayerDatabase()
                .getOrCreate(player)
                .getFavorites()
                .stream()
                .map(id -> {
                    Optional<Head> head = HeadAPI.findHeadById(id).join();
                    if (head.isPresent()) {
                        return head;
                    } else {
                        if (HeadDB.getInstance().getCfg().isLocalHeadsEnabled()) {
                            return computeLocalHead(player, calculateDate).join();
                        } else {
                            return Optional.<Head>empty();
                        }
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet()), executor);
    }

    /**
     * Retrieve a {@link Set} of favorite heads for the specified {@link UUID player id}.
     * Note that this calculates the heads on every call.
     *
     * @param player The player uuid
     * @return {@link Set<Head> Favorite Heads}
     */
    @NotNull
    public static CompletableFuture<Set<Head>> findFavoriteHeads(UUID player) {
        return findFavoriteHeads(player, false);
    }

    /**
     * Retrieve a {@link Stream} of {@link Head} within the main {@link HeadDatabase}.
     *
     * @return The streamed heads
     */
    @NotNull
    public static Stream<Head> streamHeads() {
        return getAllHeads().stream();
    }

    /**
     * Retrieve a {@link List} of {@link Head} within the main {@link HeadDatabase}.
     *
     * @return {@link List<Head> Heads}
     */
    @NotNull
    public static List<Head> getAllHeads() {
        return database.getHeads();
    }

    /**
     * Retrieve the total amount of {@link Head heads} present in the main {@link HeadDatabase}.
     *
     * @return Amount of heads
     */
    public static int countTotalHeads() {
        return getAllHeads().size();
    }

    /**
     * Returns true when the database is ready with all heads cached.
     *
     * @return If the database has cached all heads.
     */
    public boolean isDatabaseReady() {
        return database.isReady();
    }

    /**
     * Returns the {@link ExecutorService} responsible for
     *
     * @return The executor service for the api.
     */
    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * Retrieve the main {@link HeadDatabase} used by the plugin.
     *
     * @return {@link HeadDatabase Database}
     */
    @NotNull
    public static HeadDatabase getHeadDatabase() {
        return database;
    }

}