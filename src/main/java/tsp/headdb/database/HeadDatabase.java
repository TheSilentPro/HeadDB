package tsp.headdb.database;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tsp.headdb.api.Head;
import tsp.headdb.event.DatabaseUpdateEvent;
import tsp.headdb.util.Log;
import tsp.headdb.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

/**
 * This is the Database that holds all heads
 *
 * @author TheSilentPro
 */
public class HeadDatabase {

    private final JavaPlugin plugin;
    private final Map<Category, List<Head>> HEADS = new HashMap<>();
    private long refresh;
    private int timeout;
    private long updated;

    public HeadDatabase(JavaPlugin plugin) {
        this.plugin = plugin;
        this.refresh = 3600;
        this.timeout = 5000;
    }

    public Head getHeadByValue(String value) {
        List<Head> heads = getHeads();
        for (Head head : heads) {
            if (head.getValue().equals(value)) {
                return head;
            }
        }

        return null;
    }

    public Head getHeadByID(int id) {
        List<Head> heads = getHeads();
        for (Head head : heads) {
            if (head.getId() == id) {
                return head;
            }
        }

        return null;
    }

    public Head getHeadByUniqueId(UUID uuid) {
        List<Head> heads = getHeads();
        for (Head head : heads) {
            if (head.getUniqueId().equals(uuid)) {
                return head;
            }
        }

        return null;
    }

    public List<Head> getHeadsByTag(String tag) {
        List<Head> result = new ArrayList<>();
        List<Head> heads = getHeads();
        tag = tag.toLowerCase(Locale.ROOT);
        for (Head head : heads) {
            for (String t : head.getTags()) {
                if (t.toLowerCase(Locale.ROOT).contains(tag)) {
                    result.add(head);
                }
            }
        }

        return result;
    }

    public List<Head> getHeadsByName(Category category, String name) {
        List<Head> result = new ArrayList<>();
        List<Head> heads = getHeads(category);
        for (Head head : heads) {
            String hName = ChatColor.stripColor(head.getName().toLowerCase(Locale.ROOT));
            if (hName.contains(ChatColor.stripColor(name.toLowerCase(Locale.ROOT)))) {
                result.add(head);
            }
        }

        return result;
    }

    public List<Head> getHeadsByName(String name) {
        List<Head> result = new ArrayList<>();
        for (Category category : Category.values()) {
            result.addAll(getHeadsByName(category, name));
        }

        return result;
    }

    public List<Head> getHeads(Category category) {
        return HEADS.get(category);
    }

    /**
     * Gets all heads from the cache if available.
     *
     * @return List containing each head in its category.
     */
    @Nonnull
    public List<Head> getHeads() {
        if (HEADS.isEmpty() || isLastUpdateOld()) {
            // Technically this should never be reached due to the update task in the main class.
            updateAsync(result -> {
                if (result != null) {
                    for (Category category : result.keySet()) {
                        HEADS.put(category, result.get(category));
                    }
                }
            });
        }

        List<Head> heads = new ArrayList<>();
        for (Category category : HEADS.keySet()) {
            heads.addAll(HEADS.get(category));
        }
        return heads;
    }

    public void getHeadsNoCacheAsync(Consumer<Map<Category, List<Head>>> resultSet) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task -> {
            Log.debug("[" + plugin.getName() + "] Updating database... ");
            Map<Category, List<Head>> result = new HashMap<>();
            Category[] categories = Category.cache;

            int id = 1;
            for (Category category : categories) {
                Log.debug("Caching heads from: " + category.getName());
                long start = System.currentTimeMillis();
                List<Head> heads = new ArrayList<>();
                try {
                    String line;
                    StringBuilder response = new StringBuilder();

                    URLConnection connection = new URL("https://minecraft-heads.com/scripts/api.php?cat=" + category.getName() + "&tags=true").openConnection();
                    connection.setConnectTimeout(timeout);
                    connection.setRequestProperty("User-Agent", plugin.getName() + "-DatabaseUpdater");
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                    }
                    JSONParser parser = new JSONParser();
                    JSONArray array = (JSONArray) parser.parse(response.toString());
                    for (Object o : array) {
                        JSONObject obj = (JSONObject) o;
                        String rawUUID = obj.get("uuid").toString();

                        UUID uuid;
                        if (Utils.validateUniqueId(rawUUID)) {
                            uuid = UUID.fromString(rawUUID);
                        } else {
                            uuid = UUID.randomUUID();
                        }

                        Head head = new Head(id)
                                .name(obj.get("name").toString())
                                .uniqueId(uuid)
                                .value(obj.get("value").toString())
                                .tags(obj.get("tags") != null ? obj.get("tags").toString() : "None")
                                .category(category);

                        id++;
                        heads.add(head);
                    }

                    long elapsed = (System.currentTimeMillis() - start);
                    Log.debug(category.getName() + " -> Done! Time: " + elapsed + "ms (" + TimeUnit.MILLISECONDS.toSeconds(elapsed) + "s)");
                } catch (ParseException | IOException e) {
                    Log.error("[" + plugin.getName() + "] Failed to fetch heads (no-cache) | Stack Trace:");
                    e.printStackTrace();
                }

                updated = System.nanoTime();
                result.put(category, heads);
            }

            resultSet.accept(result);
        });
    }

    public void updateAsync(Consumer<Map<Category, List<Head>>> result) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task -> getHeadsNoCacheAsync(heads -> {
            if (heads == null) {
                Log.error("[" + plugin.getName() + "] Failed to update database! Check above for any errors.");
                result.accept(null);
                return;
            }

            HEADS.clear();
            HEADS.putAll(heads);
            result.accept(heads);
            Bukkit.getPluginManager().callEvent(new DatabaseUpdateEvent(this, heads));
        }));
    }

    /**
     * Get the last time the database was updated.
     *
     * @return Last update in seconds
     */
    public long getLastUpdate() {
        long now = System.nanoTime();
        long elapsed = now - updated;
        return TimeUnit.NANOSECONDS.toSeconds(elapsed);
    }

    /**
     * Checks if the update is past the refresh time
     *
     * @return Whether the update is old
     */
    public boolean isLastUpdateOld() {
        return getLastUpdate() >= refresh;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public long getRefresh() {
        return refresh;
    }

    public void setRefresh(long refresh) {
        this.refresh = refresh;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

}
