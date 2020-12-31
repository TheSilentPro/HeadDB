package tsp.headdb.api;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import tsp.headdb.HeadDB;
import tsp.headdb.database.Category;
import tsp.headdb.database.HeadDatabase;
import tsp.headdb.inventory.InventoryUtils;

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

    private static final HeadDatabase database = new HeadDatabase();

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
    public static void openDatabase(Player player, Category category) {
        InventoryUtils.openCategoryDatabase(player, category);
    }

    /**
     * Opens the database with results of a specific search term
     *
     * @param player Target player
     * @param search Search term
     */
    public static void openDatabase(Player player, String search) {
        InventoryUtils.openSearchDatabase(player, search);
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
    public static Head getHeadByUUID(UUID uuid) {
        return database.getHeadByUUID(uuid);
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

    /**
     * Add a {@link Head} to a players favorites
     *
     * @param uuid The UUID of the player
     * @param id The ID of the head
     */
    public static void addFavoriteHead(UUID uuid, int id) {
        List<Integer> favs = HeadDB.getInstance().getPlayerdata().getIntegerList(uuid.toString() + ".favorites");
        if (!favs.contains(id)) {
            favs.add(id);
        }
        HeadDB.getInstance().getPlayerdata().set(uuid.toString() + ".favorites", favs);
    }

    /**
     * Remove a {@link Head} from a players favorites
     *
     * @param uuid The UUID of the player
     * @param id The ID of the head
     */
    public static void removeFavoriteHead(UUID uuid, int id) {
        List<Integer> favs = HeadDB.getInstance().getPlayerdata().getIntegerList(uuid.toString() + ".favorites");
        for (int i = 0; i < favs.size(); i++) {
            if (favs.get(i) == id) {
                favs.remove(i);
                break;
            }
        }
        HeadDB.getInstance().getPlayerdata().set(uuid.toString() + ".favorites", favs);
    }

    /**
     * Retrieve a {@link List} of favorite {@link Head} for a player
     *
     * @param uuid The UUID of the player
     * @return List of favorite heads
     */
    public static List<Head> getFavoriteHeads(UUID uuid) {
        List<Head> heads = new ArrayList<>();
        List<Integer> ids = HeadDB.getInstance().getPlayerdata().getIntegerList(uuid.toString() + ".favorites");
        for (int id : ids) {
            Head head = getHeadByID(id);
            heads.add(head);
        }

        return heads;
    }

    /**
     * Retrieve a {@link List} of local heads.
     * These heads are from players that have joined the server at least once.
     *
     * @return List of {@link LocalHead}'s
     */
    public static List<LocalHead> getLocalHeads() {
        List<LocalHead> heads = new ArrayList<>();
        for (String key : HeadDB.getInstance().getPlayerdata().singleLayerKeySet()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(key));
            heads.add(new LocalHead(player.getUniqueId())
                    .withName(player.getName()));
        }

        return heads;
    }

    /**
     * Update the Head Database
     */
    public static void updateDatabase() {
        database.update();
    }

}
