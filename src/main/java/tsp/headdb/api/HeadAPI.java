package tsp.headdb.api;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
import java.util.ArrayList;
import java.util.List;
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
    public static void openDatabase(Player player) {
        InventoryUtils.openDatabase(player);
    }

    /**
     * Opens a specific category of the database for a player
     * 
     * @param player Target player
     * @param category Category to open
     */
    public static void openCategoryDatabase(Player player, Category category) {
        InventoryUtils.openCategoryDatabase(player, category);
    }

    /**
     * Opens the database with results of a specific search term
     *
     * @param player Target player
     * @param search Search term
     */
    public static void openSearchDatabase(Player player, String search) {
        InventoryUtils.openSearchDatabase(player, search);
    }

    /**
     * Opens the database with results of a specific tag search term
     *
     * @param player Target player
     * @param tag Tag search term
     */
    public static void openTagSearchDatabase(Player player, String tag) {
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
    public static Head getHeadByUniqueId(UUID uuid) {
        return database.getHeadByUniqueId(uuid);
    }

    /**
     * Retrieve a {@link List} of {@link Head}'s by their tag
     *
     * @param tag The tag
     * @return List of heads
     */
    @Nonnull
    public static List<Head> getHeadsByTag(String tag) {
        return database.getHeadsByTag(tag);
    }

    /**
     * Retrieves a {@link List} of {@link Head}'s matching a name
     *
     * @param name The name to match for
     * @return List of heads
     */
    @Nonnull
    public static List<Head> getHeadsByName(String name) {
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
    public static List<Head> getHeadsByName(Category category, String name) {
        return database.getHeadsByName(category, name);
    }

    /**
     * Retrieve a {@link Head} by it's value
     *
     * @param value The texture value
     * @return The head
     */
    @Nullable
    public static Head getHeadByValue(String value) {
        return database.getHeadByValue(value);
    }

    /**
     * Retrieve a {@link List} of {@link Head}'s in a specific {@link Category}
     *
     * @param category The category to search in
     * @return List of heads
     */
    @Nonnull
    public static List<Head> getHeads(Category category) {
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
    public static void addFavoriteHead(UUID uuid, String textureValue) {
        HeadDB.getInstance().getPlayerData().modifyFavorite(uuid, textureValue, PlayerDataFile.ModificationType.SET);
    }

    /**
     * Remove a favorite {@link Head} from the player
     *
     * @param uuid The player's unique id
     * @param textureValue The head's texture value
     */
    public static void removeFavoriteHead(UUID uuid, String textureValue) {
        HeadDB.getInstance().getPlayerData().modifyFavorite(uuid, textureValue, PlayerDataFile.ModificationType.REMOVE);
    }

    /**
     * Retrieve a {@link List} of favorite {@link Head}'s for the player
     *
     * @param uuid The player's unique id
     * @return List of favorite {@link Head}'s for the player
     */
    @Nonnull
    public static List<Head> getFavoriteHeads(UUID uuid) {
        return HeadDB.getInstance().getPlayerData().getFavoriteHeadsByTexture(uuid).stream()
                .map(HeadAPI::getHeadByValue)
                .collect(Collectors.toList());
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
                .collect(Collectors.toList());
    }

}
