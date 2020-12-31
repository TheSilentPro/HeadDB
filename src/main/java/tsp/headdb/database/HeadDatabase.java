package tsp.headdb.database;

import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tsp.headdb.HeadDB;
import tsp.headdb.api.Head;
import tsp.headdb.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * This is the Database that holds all heads
 *
 * @author TheSilentPro
 */
public class HeadDatabase {

    private final Map<Category, List<Head>> HEADS = new HashMap<>();
    private final String URL = "https://minecraft-heads.com/scripts/api.php?cat=";
    private final String TAGS = "&tags=true";
    private long updated;
    
    public HeadDatabase() {}

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

    public Head getHeadByUUID(UUID uuid) {
        List<Head> heads = getHeads();
        for (Head head : heads) {
            if (head.getUUID().equals(uuid)) {
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

    public Map<Category, List<Head>> getHeadsNoCache() {
        Map<Category, List<Head>> result = new HashMap<>();
        List<Category> categories = Category.getCategories();

        int id = 1;
        for (Category category : categories) {
            Log.debug("Caching heads from: " + category.getName());
            List<Head> heads = new ArrayList<>();
            try {
                String line;
                StringBuilder response = new StringBuilder();

                URLConnection connection = new URL(URL + category.getName() + TAGS).openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestProperty("User-Agent", "HeadDB");
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }
                JSONParser parser = new JSONParser();
                JSONArray array = (JSONArray) parser.parse(response.toString());
                for (Object o : array) {
                    JSONObject obj = (JSONObject) o;
                    Head head = new Head(id)
                            .withName(obj.get("name").toString())
                            .withUUID(UUID.fromString(obj.get("uuid").toString()))
                            .withValue(obj.get("value").toString())
                            .withTags(obj.get("tags") != null ? obj.get("tags").toString() : "None")
                            .withCategory(category);

                    id++;
                    heads.add(head);
                }
            } catch (ParseException | IOException e) {
                Log.error("Failed to fetch heads (no-cache) | Stack Trace:");
                e.printStackTrace();
            }

            updated = System.nanoTime();
            result.put(category, heads);
        }

        return result;
    }

    public void update() {
        Map<Category, List<Head>> heads = getHeadsNoCache();
        HEADS.clear();
        for (Map.Entry<Category, List<Head>> entry : heads.entrySet()) {
            HEADS.put(entry.getKey(), entry.getValue());
        }
    }

    public long getLastUpdate() {
        long now = System.nanoTime();
        long elapsed = now - updated;
        return TimeUnit.NANOSECONDS.toSeconds(elapsed);
    }

    public boolean isLastUpdateOld() {
        if (HeadDB.getInstance().getCfg() == null && getLastUpdate() >= 3600) return true;
        return getLastUpdate() >= HeadDB.getInstance().getCfg().getLong("refresh");
    }

}
