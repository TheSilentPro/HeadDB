package tsp.headdb.api;

import org.bukkit.plugin.java.JavaPlugin;
import tsp.headdb.api.model.Category;
import tsp.headdb.api.model.Head;
import tsp.headdb.api.provider.HeadProvider;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

/**
 * @author TheSilentPro (Silent)
 */
public class HeadDatabase {

    private final JavaPlugin plugin;
    private final ExecutorService executor;
    private final CopyOnWriteArrayList<Head> heads;
    private final HeadProvider provider;
    private long timestamp;
    private volatile boolean ready;

    public HeadDatabase(JavaPlugin plugin, ExecutorService executor, HeadProvider provider) {
        this.plugin = plugin;
        this.executor = executor;
        this.provider = provider;
        this.heads = new CopyOnWriteArrayList<>();
        this.ready = false;
    }

    public List<Head> getHeads() {
        return Collections.unmodifiableList(heads);
    }

    public CompletableFuture<List<Head>> getHeadsNoCache() {
        this.ready = false;
        this.heads.clear();
        return CompletableFuture.supplyAsync(() -> {
            heads.addAll(provider.fetchHeads(executor).join().heads());
            this.ready = true;
            timestamp = System.currentTimeMillis();
            return Collections.unmodifiableList(heads);
        }, executor);
    }

    public boolean isReady() {
        return ready;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

}