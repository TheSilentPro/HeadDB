package tsp.headdb.api;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tsp.headdb.HeadDB;
import tsp.headdb.implementation.Category;
import tsp.headdb.implementation.Head;
import tsp.headdb.implementation.HeadDatabase;
import tsp.headdb.implementation.LocalHead;
import tsp.headdb.inventory.InventoryUtils;
import tsp.headdb.storage.PlayerDataFile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class provides simple methods
 * for interacting with the HeadDB plugin
 *
 * @author TheSilentPro
 */
public final class HeadAPI {

    private HeadAPI() {}

    private static final String VALIDATION_PLAYER_NULL = "Player can not be null!";
    private static final String VALIDATION_CATEGORY_NULL = "Category can not be null!";
    private static final String VALIDATION_UUID_NULL = "UUID can not be null!";
    private static final String VALIDATION_VALUE_NULL = "Value can not be null!";

    /**
     * Main {@link HeadDatabase} that he HeadDB plugin uses.
     */
    private static final HeadDatabase database = new HeadDatabase(HeadDB.getInstance());

    /**
     * Retrieves the main {@link HeadDatabase}
     *
     * @return Head Database
     */
    public static HeadDatabase getDatabase() {
        return database;
    }

    /**
     * Opens the database for a player
     * 
     * @param player Target player
     */
    public static void openDatabase(@Nonnull Player player) {
        Validate.notNull(player, VALIDATION_PLAYER_NULL);

        InventoryUtils.openDatabase(player);
    }

    /**
     * Opens a specific category of the database for a player
     * 
     * @param player Target player
     * @param category Category to open
     */
    public static void openCategoryDatabase(@Nonnull Player player, @Nonnull Category category) {
        Validate.notNull(player, VALIDATION_PLAYER_NULL);
        Validate.notNull(category, VALIDATION_CATEGORY_NULL);

        InventoryUtils.openCategoryDatabase(player, category);
    }

    /**
     * Opens the database with results of a specific search term
     *
     * @param player Target player
     * @param search Search term
     */
    public static void openSearchDatabase(@Nonnull Player player, @Nonnull String search) {
        Validate.notNull(player, VALIDATION_PLAYER_NULL);
        Validate.notNull(search, "Search can not be null!");

        InventoryUtils.openSearchDatabase(player, search);
    }

    /**
     * Opens the database with results of a specific tag search term
     *
     * @param player Target player
     * @param tag Tag search term
     */
    public static void openTagSearchDatabase(@Nonnull Player player, @Nonnull String tag) {
        Validate.notNull(player, VALIDATION_PLAYER_NULL);
        Validate.notNull(tag, "Tag can not be null!");

        InventoryUtils.openTagSearchDatabase(player, tag);
    }

    /**
     * Retrieve a {@link Head} by it's ID
     *
     * @param id The ID of the head
     * @return The head
     */
    @Nullable
    public static Head getHeadByID(int id) {
        return database.getHeadByID(id);
    }

    /**
     * Retrieve a {@link Head} by it's UUID
     *
     * @param uuid The UUID of the head
     * @return The head
     */
    @Nullable
    public static Head getHeadByUniqueId(@Nonnull UUID uuid) {
        Validate.notNull(uuid, VALIDATION_UUID_NULL);

        return database.getHeadByUniqueId(uuid);
    }

    /**
     * Retrieve a {@link List} of {@link Head}'s by their tag
     *
     * @param tag The tag
     * @return List of heads
     */
    @Nonnull
    public static List<Head> getHeadsByTag(@Nonnull String tag) {
        Validate.notNull(tag, "Tag can not be null!");

        return database.getHeadsByTag(tag);
    }

    /**
     * Retrieves a {@link List} of {@link Head}'s matching a name
     *
     * @param name The name to match for
     * @return List of heads
     */
    @Nonnull
    public static List<Head> getHeadsByName(@Nonnull String name) {
        Validate.notNull(name, "Name can not be null!");

        return database.getHeadsByName(name);
    }

    /**
     * Retrieves a {@link List} of {@link Head}'s in a {@link Category} matching a name
     *
     * @param category The category to search in
     * @param name The name to match for
     * @return List of heads
     */
    @Nonnull
    public static List<Head> getHeadsByName(@Nonnull Category category, @Nonnull String name) {
        Validate.notNull(category, VALIDATION_CATEGORY_NULL);
        Validate.notNull(name, "Name can not be null!");

        return database.getHeadsByName(category, name);
    }

    /**
     * Retrieve a {@link Head} by it's value
     *
     * @param value The texture value
     * @return The head
     */
    @Nullable
    public static Head getHeadByValue(@Nonnull String value) {
        Validate.notNull(value, VALIDATION_VALUE_NULL);

        return database.getHeadByValue(value);
    }

    /**
     * Retrieve a {@link List} of {@link Head}'s in a specific {@link Category}
     *
     * @param category The category to search in
     * @return List of heads
     */
    @Nonnull
    public static List<Head> getHeads(@Nonnull Category category) {
        Validate.notNull(category, VALIDATION_CATEGORY_NULL);

        return database.getHeads(category);
    }

    /**
     * Retrieve a {@link List} of all {@link Head}'s
     *
     * @return List of all heads
     */
    @Nonnull
    public static List<Head> getHeads() {
        return database.getHeads();
    }

    /**
     * Add a favorite {@link Head} to the player
     *
     * @param uuid The player's unique id
     * @param textureValue The head's texture value
     */
    public static void addFavoriteHead(@Nonnull UUID uuid, @Nonnull String textureValue) {
        Validate.notNull(uuid, VALIDATION_UUID_NULL);
        Validate.notNull(textureValue, VALIDATION_VALUE_NULL);

        HeadDB.getInstance().getPlayerData().modifyFavorite(uuid, textureValue, PlayerDataFile.ModificationType.SET);
    }

    /**
     * Remove a favorite {@link Head} from the player
     *
     * @param uuid The player's unique id
     * @param textureValue The head's texture value
     */
    public static void removeFavoriteHead(@Nonnull UUID uuid, @Nonnull String textureValue) {
        Validate.notNull(uuid, VALIDATION_UUID_NULL);
        Validate.notNull(textureValue, VALIDATION_VALUE_NULL);

        HeadDB.getInstance().getPlayerData().modifyFavorite(uuid, textureValue, PlayerDataFile.ModificationType.REMOVE);
    }

    /**
     * Retrieve a {@link List} of favorite {@link Head}'s for the player
     *
     * @param uuid The player's unique id
     * @return List of favorite {@link Head}'s for the player
     */
    @Nonnull
    public static List<Head> getFavoriteHeads(@Nonnull UUID uuid) {
        Validate.notNull(uuid, VALIDATION_UUID_NULL);

        return HeadDB.getInstance().getPlayerData().getFavoriteHeadsByTexture(uuid).stream()
                .map(HeadAPI::getHeadByValue)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Retrieve a list of {@link LocalHead}'s.
     * These are heads from players that have joined the server at least once.
     * Requires config option localHeads = true
     *
     * @return List of {@link LocalHead}'s
     */
    @Nonnull
    public static List<LocalHead> getLocalHeads() {
        return HeadDB.getInstance().getPlayerData().getEntries().stream()
                .map(entry -> Bukkit.getOfflinePlayer(UUID.fromString(entry)))
                .map(player -> new LocalHead(player.getUniqueId()).name(player.getName()))
                .toList();
    }

}
