package tsp.headdb.api;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import tsp.headdb.HeadDB;
import tsp.headdb.database.Category;
import tsp.headdb.database.HeadDatabase;
import tsp.headdb.inventory.InventoryUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HeadAPI {

    public static void openDatabase(Player player) {
        InventoryUtils.openDatabase(player);
    }

    public static void openDatabase(Player player, Category category) {
        InventoryUtils.openCategoryDatabase(player, category);
    }

    public static void openDatabase(Player player, String search) {
        InventoryUtils.openSearchDatabase(player, search);
    }

    public static Head getHeadByID(int id) {
        return HeadDatabase.getHeadByID(id);
    }

    public static Head getHeadByUUID(UUID uuid) {
        return HeadDatabase.getHeadByUUID(uuid);
    }

    public static List<Head> getHeadsByName(String name) {
        return HeadDatabase.getHeadsByName(name);
    }

    public static List<Head> getHeadsByName(Category category, String name) {
        return HeadDatabase.getHeadsByName(category, name);
    }

    public static Head getHeadByValue(String value) {
        return HeadDatabase.getHeadByValue(value);
    }

    public static List<Head> getHeads(Category category) {
        return HeadDatabase.getHeads(category);
    }

    public static List<Head> getHeads() {
        return HeadDatabase.getHeads();
    }

    public static void addFavoriteHead(UUID uuid, int id) {
        List<Integer> favs = HeadDB.getPlayerdata().getIntList(uuid.toString() + ".favorites");
        if (!favs.contains(id)) {
            favs.add(id);
        }
        HeadDB.getPlayerdata().set(uuid.toString() + ".favorites", favs);
        HeadDB.getPlayerdata().save();
    }

    public static void removeFavoriteHead(UUID uuid, int id) {
        List<Integer> favs = HeadDB.getPlayerdata().getIntList(uuid.toString() + ".favorites");
        for (int i = 0; i < favs.size(); i++) {
            if (favs.get(i) == id) {
                favs.remove(i);
                break;
            }
        }
        HeadDB.getPlayerdata().set(uuid.toString() + ".favorites", favs);
        HeadDB.getPlayerdata().save();
    }

    public static List<Head> getFavoriteHeads(UUID uuid) {
        List<Head> heads = new ArrayList<>();
        List<Integer> ids = HeadDB.getPlayerdata().getIntList(uuid.toString() + ".favorites");
        for (int id : ids) {
            Head head = getHeadByID(id);
            heads.add(head);
        }

        return heads;
    }

    public static List<LocalHead> getLocalHeads() {
        List<LocalHead> heads = new ArrayList<>();
        for (String key : HeadDB.getPlayerdata().getKeys(false)) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(key));
            heads.add(new LocalHead.Builder()
                    .withUUID(player.getUniqueId())
                    .withName(player.getName())
                    .build());
        }

        return heads;
    }

    public static void updateDatabase() {
        HeadDatabase.update();
    }

}
