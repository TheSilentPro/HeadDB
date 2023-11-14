package tsp.headdb.implementation.head;

import org.bukkit.plugin.java.JavaPlugin;
import tsp.headdb.implementation.category.Category;
import tsp.headdb.implementation.requester.HeadProvider;
import tsp.headdb.implementation.requester.Requester;
import tsp.helperlite.scheduler.promise.Promise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HeadDatabase {

    private final JavaPlugin plugin;
    private final Requester requester;
    private final ConcurrentHashMap<Category, List<Head>> heads;
    private long timestamp;

    public HeadDatabase(JavaPlugin plugin, HeadProvider provider) {
        this.plugin = plugin;
        this.requester = new Requester(plugin, provider);
        this.heads = new ConcurrentHashMap<>();

        // Fill empty
        for (Category cat : Category.VALUES) {
            heads.put(cat, new ArrayList<>());
        }
    }

    public Map<Category, List<Head>> getHeads() {
        return heads;
    }

    public Promise<HeadResult> getHeadsNoCache() {
        return Promise.supplyingAsync(() -> {
            long start = System.currentTimeMillis();
            Map<Category, List<Head>> result = new HashMap<>();
            for (Category category : Category.VALUES) {
                result.put(category, requester.fetchAndResolve(category));
            }

            return new HeadResult(System.currentTimeMillis() - start, result);
        });
    }

    public Promise<HeadResult> update() {
        return Promise.start()
                .thenComposeAsync(compose -> getHeadsNoCache())
                .thenApplyAsync(result -> {
                    heads.clear();
                    heads.putAll(result.heads());
                    timestamp = System.currentTimeMillis();
                    return result;
        });
    }

    public long getTimestamp() {
        return timestamp;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Requester getRequester() {
        return requester;
    }

}