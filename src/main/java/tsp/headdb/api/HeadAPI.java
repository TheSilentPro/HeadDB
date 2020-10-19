package tsp.headdb.api;

import org.bukkit.entity.Player;
import tsp.headdb.database.Category;
import tsp.headdb.database.HeadDatabase;
import tsp.headdb.inventory.InventoryUtils;

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

    public static void updateDatabase() {
        HeadDatabase.update();
    }

}
