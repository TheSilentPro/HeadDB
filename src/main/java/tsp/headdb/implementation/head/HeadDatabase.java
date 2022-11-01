package tsp.headdb.implementation.head;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import tsp.headdb.implementation.category.Category;
import tsp.headdb.implementation.requester.Requester;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class HeadDatabase {

    private final JavaPlugin plugin;
    private final BukkitScheduler scheduler;
    private final Requester requester;
    private final Map<Category, List<Head>> heads;
    private long timestamp;
    private int size;

    public HeadDatabase(JavaPlugin plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
        this.requester = new Requester(plugin);
        this.heads = Collections.synchronizedMap(new EnumMap<>(Category.class));
    }

    public Map<Category, List<Head>> getHeads() {
        return heads;
    }

    public void getHeadsNoCache(Consumer<Map<Category, List<Head>>> heads) {
        getScheduler().runTaskAsynchronously(plugin, () -> {
            Map<Category, List<Head>> result = new HashMap<>();
            for (Category category : Category.VALUES) {
                try {
                    requester.fetchAndResolve(category, response -> result.put(category, response));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            heads.accept(result);
        });
    }

    public void update() {
        getHeadsNoCache(result -> {
            heads.putAll(result);
            timestamp = System.currentTimeMillis();
            size = heads.values().size();
        });
    }

    public int getSize() {
        return size;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public BukkitScheduler getScheduler() {
        return scheduler;
    }

    public Requester getRequester() {
        return requester;
    }

}