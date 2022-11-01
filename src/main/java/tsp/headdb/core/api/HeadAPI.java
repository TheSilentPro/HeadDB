package tsp.headdb.core.api;

import tsp.headdb.HeadDB;
import tsp.headdb.implementation.category.Category;
import tsp.headdb.implementation.head.Head;
import tsp.headdb.implementation.head.HeadDatabase;

import java.util.List;
import java.util.Map;

public final class HeadAPI {

    private HeadAPI() {}

    private static final HeadDatabase database = new HeadDatabase(HeadDB.getInstance());

    public static List<Head> getHeads(Category category) {
        return database.getHeads().get(category);
    }

    public static Map<Category, List<Head>> getHeads() {
        return database.getHeads();
    }

    public static int getTotalHeads() {
        return database.getSize();
    }

    public static HeadDatabase getDatabase() {
        return database;
    }

}
