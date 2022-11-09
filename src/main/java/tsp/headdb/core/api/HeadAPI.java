package tsp.headdb.core.api;

import org.bukkit.Bukkit;

import tsp.headdb.HeadDB;
import tsp.headdb.core.util.Utils;
import tsp.headdb.implementation.category.Category;
import tsp.headdb.implementation.head.Head;
import tsp.headdb.implementation.head.HeadDatabase;
import tsp.headdb.implementation.head.LocalHead;
import tsp.headdb.implementation.requester.HeadProvider;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * The main {@link HeadDatabase}.
     */
    private static final HeadDatabase database = new HeadDatabase(HeadDB.getInstance(), HeadProvider.HEAD_STORAGE);

    /**
     * Retrieve a {@link List} of {@link Head} matching the name.
     *
     * @param name The name to match against
     * @param lenient Whether the filter should be lenient when matching
     * @return {@link List<Head> Heads}
     */
    @Nonnull
    public static List<Head> getHeadsByName(String name, boolean lenient) {
        return getHeads().stream().filter(head -> (lenient ? Utils.matches(head.getName(), name) : head.getName().equalsIgnoreCase(name))).collect(Collectors.toList());
    }

    /**
     * Retrieve a {@link List} of {@link Head} matching the name.
     *
     * @param name The name to match against
     * @return {@link List<Head> Heads}
     */
    @Nonnull
    public static List<Head> getHeadsByName(String name) {
        return getHeadsByName(name, true);
    }

    /**
     * Retrieve a {@link Head} by its exact name.
     *
     * @param name The name to look for
     * @param lenient Whether the filter to be lenient when matching
     * @return The {@link Head}, else empty
     */
    public static Optional<Head> getHeadByExactName(String name, boolean lenient) {
        return getHeads().stream().filter(head -> (lenient ? Utils.matches(head.getName(), name) : head.getName().equalsIgnoreCase(name))).findAny();
    }

    /**
     * Retrieve a {@link Head} by its exact name.
     *
     * @param name The name to look for
     * @return The {@link Head}, else empty
     */
    @Nonnull
    public static Optional<Head> getHeadByExactName(String name) {
        return getHeadByExactName(name, false);
    }

    /**
     * Retrieve a {@link Head} by its id.
     *
     * @param id The id to look for
     * @return The {@link Head}, else empty
     */
    @Nonnull
    public static Optional<Head> getHeadById(int id) {
        return getHeads().stream().filter(head -> head.getId() == id).findAny();
    }

    /**
     * Retrieve a {@link Head} by its texture value.
     *
     * @param texture The texture to look for
     * @return The {@link Head}, else empty
     */
    @Nonnull
    public static Optional<Head> getHeadByTexture(String texture) {
        return getHeads().stream().filter(head -> head.getTexture().equals(texture)).findAny();
    }

    /**
     * Retrieve a {@link List} of {@link Head} within the main {@link HeadDatabase}.
     *
     * @return {@link List<Head> Heads}
     */
    @Nonnull
    public static List<Head> getHeads() {
        List<Head> result = new ArrayList<>();
        for (Category category : getHeadsMap().keySet()) {
            result.addAll(getHeads(category));
        }

        return result;
    }

    /**
     * Retrieve a {@link List} of {@link Head} within a {@link Category}.
     *
     * @param category The category to retrieve the heads from
     * @return {@link List<Head> Heads}
     */
    @Nonnull
    public static List<Head> getHeads(Category category) {
        return getHeadsMap().get(category);
    }

    /**
     * Retrieve an unmodifiable view of the database head map.
     *
     * @return The map
     */
    @Nonnull
    public static Map<Category, List<Head>> getHeadsMap() {
        return Collections.unmodifiableMap(database.getHeads());
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
     * Note that this calculates the heads on every try.
     *
     * @return {@link Set<LocalHead> Local Heads}
     */
    @Nonnull
    public static Set<LocalHead> getLocalHeads() {
        return Arrays.stream(Bukkit.getOfflinePlayers()).map(player -> new LocalHead(player.getUniqueId(), player.getName())).collect(Collectors.toSet());
    }

    /**
     * Retrieve the main {@link HeadDatabase} used by the plugin.
     *
     * @return {@link HeadDatabase Database}
     */
    @Nonnull
    public static HeadDatabase getDatabase() {
        return database;
    }

}
