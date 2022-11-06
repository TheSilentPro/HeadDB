package tsp.headdb.core.api;

import tsp.headdb.HeadDB;
import tsp.headdb.implementation.category.Category;
import tsp.headdb.implementation.head.Head;
import tsp.headdb.implementation.head.HeadDatabase;
import tsp.headdb.implementation.requester.HeadProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class HeadAPI {

    private HeadAPI() {}

    private static final HeadDatabase database = new HeadDatabase(HeadDB.getInstance(), HeadProvider.HEAD_STORAGE);

    public static synchronized Optional<Head> getHeadById(int id) {
        return getHeads().stream().filter(head -> head.getId() == id).findAny();
    }

    public static synchronized Optional<Head> getHeadByTexture(String texture) {
        return getHeads().stream().filter(head -> head.getTexture().equals(texture)).findAny();
    }

    public static List<Head> getHeads() {
        List<Head> result = new ArrayList<>();
        for (Category category : getHeadsMap().keySet()) {
            result.addAll(getHeads(category));
        }

        return result;
    }

    public static List<Head> getHeads(Category category) {
        return getHeadsMap().get(category);
    }

    public static synchronized Map<Category, List<Head>> getHeadsMap() {
        return Collections.unmodifiableMap(database.getHeads());
    }

    public static int getTotalHeads() {
        return getHeads().size();
    }

    public static HeadDatabase getDatabase() {
        return database;
    }

}
