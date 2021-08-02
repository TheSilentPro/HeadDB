package tsp.headdb.database;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tsp.headdb.api.Head;
import tsp.headdb.util.Log;
import tsp.headdb.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

/**
 * This is the Database that holds all heads
 *
 * @author TheSilentPro
 */
public class HeadDatabase {

    private final JavaPlugin plugin;
    private final long refresh;
    private int timeout;
    private final Map<Category, List<Head>> HEADS = new HashMap<>();
    private long updated;

    public HeadDatabase(JavaPlugin plugin) {
        this.plugin = plugin;
        this.refresh = 3600;
        this.timeout = 5000;
    }

    public HeadDatabase(JavaPlugin plugin, long refresh) {
        this.plugin = plugin;
        this.refresh = refresh;
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

    public List<Head> getHeads() {
        if (!HEADS.isEmpty() && !isLastUpdateOld()) {
            List<Head> heads = new ArrayList<>();
            for (Category category : HEADS.keySet()) {
                heads.addAll(HEADS.get(category));
            }
            return heads;
        }

        update();
        return getHeads();
    }

    /**
     * Gets all heads from the api provider
     *
     * @return Map containing each head in it's category. Returns null if the fetching failed.
     */
    @Nullable
    public Map<Category, List<Head>> getHeadsNoCache() {
        Map<Category, List<Head>> result = new HashMap<>();
        List<Category> categories = Category.getCategories();

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
                    String uuid = obj.get("uuid").toString();
                    Log.debug(!Utils.isValid(uuid) + "Invalid UUID: " + uuid);

                    Head head = new Head(id)
                            .withName(obj.get("name").toString())
                            .withUniqueId(Utils.isValid(uuid) ? UUID.fromString(uuid) : UUID.randomUUID())
                            .withValue(obj.get("value").toString())
                            .withTags(obj.get("tags") != null ? obj.get("tags").toString() : "None")
                            .withCategory(category);

                    id++;
                    heads.add(head);
                }

                long elapsed = (System.currentTimeMillis() - start);
                Log.debug(category.getName() + " -> Done! Time: " + elapsed + "ms (" + TimeUnit.MILLISECONDS.toSeconds(elapsed) + "s)");
            } catch (ParseException | IOException e) {
                Log.error("Failed to fetch heads (no-cache) | Stack Trace:");
                e.printStackTrace();
                return null;
            }

            updated = System.nanoTime();
            result.put(category, heads);
        }

        return result;
    }

    /**
     * Updates the cached heads
     *
     * @return Returns true if the update was successful.
     */
    public boolean update() {
        Map<Category, List<Head>> heads = getHeadsNoCache();
        if (heads == null) {
            Log.error("Failed to update database! Check above for any errors.");
            return false;
        }

        HEADS.clear();
        for (Map.Entry<Category, List<Head>> entry : heads.entrySet()) {
            HEADS.put(entry.getKey(), entry.getValue());
        }
        return true;
    }

    public long getLastUpdate() {
        long now = System.nanoTime();
        long elapsed = now - updated;
        return TimeUnit.NANOSECONDS.toSeconds(elapsed);
    }

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

    public JavaPlugin getPlugin() {
        return plugin;
    }

}
