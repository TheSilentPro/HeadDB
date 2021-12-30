package tsp.headdb.api;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import tsp.headdb.HeadDB;
import tsp.headdb.database.Category;
import tsp.headdb.database.HeadDatabase;
import tsp.headdb.inventory.InventoryUtils;
import tsp.headdb.storage.PlayerDataFile;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class provides simple methods
 * for interacting with the HeadDB plugin
 *
 * @author TheSilentPro
 */
public final class HeadAPI {

    private HeadAPI() {}

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

    public static List<Head> getHeadsByTag(String tag) {
        return database.getHeadsByTag(tag);
    }

    /**
     * Retrieves a {@link List} of {@link Head}'s matching a name
     *
     * @param name The name to match for
     * @return List of heads
     */
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
    public static List<Head> getHeads(Category category) {
        return database.getHeads(category);
    }

    /**
     * Retrieve a {@link List} of all {@link Head}'s
     *
     * @return List of all heads
     */
    public static List<Head> getHeads() {
        return database.getHeads();
    }
    
    public static void addFavoriteHead(UUID uuid, String textureValue) {
        HeadDB.getInstance().getPlayerData().modifyFavorite(uuid, textureValue, PlayerDataFile.ModificationType.SET);
    }
    
    public static void removeFavoriteHead(UUID uuid, String textureValue) {
        HeadDB.getInstance().getPlayerData().modifyFavorite(uuid, textureValue, PlayerDataFile.ModificationType.REMOVE);
    }
    
    public static List<Head> getFavoriteHeads(UUID uuid) {
        List<Head> result = new ArrayList<>();
        
        List<String> textures = HeadDB.getInstance().getPlayerData().getFavoriteHeadsByTexture(uuid);
        for (String texture : textures) {
            result.add(HeadAPI.getHeadByValue(texture));
        }

        return result;
    }

    public static List<LocalHead> getLocalHeads() {
        List<LocalHead> result = new ArrayList<>();
        for (String entry : HeadDB.getInstance().getPlayerData().getEntries()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(entry));
            result.add(new LocalHead(player.getUniqueId()).name(player.getName()));
        }

        return result;
    }

}
