package tsp.headdb.database;

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

public class HeadDatabase {

    private static final Map<Category, List<Head>> HEADS = new HashMap<>();
    private static final String URL = "https://minecraft-heads.com/scripts/api.php?cat=";
    private static long updated;

    public static Head getHeadByValue(String value) {
        List<Head> heads = getHeads();
        for (Head head : heads) {
            if (head.getValue().equals(value)) {
                return head;
            }
        }

        return null;
    }

    public static Head getHeadByID(int id) {
        List<Head> heads = getHeads();
        for (Head head : heads) {
            if (head.getId() == id) {
                return head;
            }
        }

        return null;
    }

    public static Head getHeadByUUID(UUID uuid) {
        List<Head> heads = getHeads();
        for (Head head : heads) {
            if (head.getUUID().equals(uuid)) {
                return head;
            }
        }

        return null;
    }

    public static List<Head> getHeadsByName(Category category, String name) {
        List<Head> result = new ArrayList<>();
        List<Head> heads = getHeads(category);
        for (Head head : heads) {
            if (head.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(head);
            }
        }

        return result;
    }

    public static List<Head> getHeadsByName(String name) {
        List<Head> result = new ArrayList<>();
        List<Head> heads = getHeads();
        for (Head head : heads) {
            if (head.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(head);
            }
        }

        return result;
    }

    public static List<Head> getHeads(Category category) {
        return HEADS.get(category);
    }

    public static List<Head> getHeads() {
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

    public static Map<Category, List<Head>> getHeadsNoCache() {
        Map<Category, List<Head>> result = new HashMap<>();
        List<Category> categories = Category.getCategories();

        int id = 1;
        for (Category category : categories) {
            Log.debug("Caching heads from: " + category.getName());
            List<Head> heads = new ArrayList<>();
            try {
                String line;
                StringBuilder response = new StringBuilder();

                URLConnection connection = new URL(URL + category.getName()).openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestProperty("User-Agent", "HeadDB");
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream()))) {
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }
                JSONParser parser = new JSONParser();
                JSONArray array = (JSONArray) parser.parse(response.toString());
                for (Object o : array) {
                    JSONObject obj = (JSONObject) o;
                    Head head = new Head.Builder()
                            .withName(obj.get("name").toString())
                            .withUUID(UUID.fromString(obj.get("uuid").toString()))
                            .withValue(obj.get("value").toString())
                            .withCategory(category)
                            .withId(id)
                            .build();

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

    public static void update() {
        Map<Category, List<Head>> heads = getHeadsNoCache();
        HEADS.clear();
        for (Map.Entry<Category, List<Head>> entry : heads.entrySet()) {
            HEADS.put(entry.getKey(), entry.getValue());
        }
    }

    public static long getLastUpdate() {
        long now = System.nanoTime();
        long elapsed = now - updated;
        return TimeUnit.NANOSECONDS.toSeconds(elapsed);
    }

    public static boolean isLastUpdateOld() {
        if (HeadDB.getCfg() == null && getLastUpdate() >= 3600) return true;
        return getLastUpdate() >= HeadDB.getCfg().getLong("refresh");
    }

}
