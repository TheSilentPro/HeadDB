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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class HeadAPI {

    private HeadAPI() {}

    private static final HeadDatabase database = new HeadDatabase(HeadDB.getInstance(), HeadProvider.HEAD_STORAGE);

    public static synchronized Optional<Head> getById(int id) {
        return getHeads().filter(head -> head.getId() == id).findFirst();
    }

    public static synchronized Stream<Head> getHeads() {
        List<Head> result = new ArrayList<>();
        for (Category category : getHeadsMap().keySet()) {
            result.addAll(getHeads(category).collect(Collectors.toList()));
        }

        return result.stream();
    }

    public static synchronized Stream<Head> getHeads(Category category) {
        return getHeadsMap().get(category).stream();
    }

    public static synchronized Map<Category, List<Head>> getHeadsMap() {
        return Collections.unmodifiableMap(database.getHeads());
    }

    public static int getTotalHeads() {
        return database.getSize();
    }

    public static HeadDatabase getDatabase() {
        return database;
    }

}
