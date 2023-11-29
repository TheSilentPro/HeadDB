package tsp.headdb.implementation.requester;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;
import tsp.headdb.HeadDB;
import tsp.headdb.core.util.Utils;
import tsp.headdb.implementation.category.Category;
import tsp.headdb.implementation.head.Head;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Responsible for requesting heads from providers.
 *
 * @author TheSilentPro
 * @see tsp.headdb.core.api.HeadAPI
 * @see tsp.headdb.implementation.head.HeadDatabase
 */
public class Requester {

    private final JavaPlugin plugin;
    private HeadProvider provider;

    public Requester(JavaPlugin plugin, HeadProvider provider) {
        this.plugin = plugin;
        this.provider = provider;
    }

    public List<Head> fetchAndResolve(Category category) {
        try {
            Response response = fetch(category);
            List<Head> result = new ArrayList<>();
            if (response.code() != 200) {
                return result;
            }

            JsonArray main = JsonParser.parseString(response.response()).getAsJsonArray();
            for (JsonElement entry : main) {
                JsonObject obj = entry.getAsJsonObject();
                int id = obj.get("id").getAsInt();

                if (plugin.getConfig().contains("blockedHeads.ids")) {
                    List<Integer> blockedIds = plugin.getConfig().getIntegerList("blockedHeads.ids");
                    if (blockedIds.contains(id)) {
                        HeadDB.getInstance().getLog().debug("Skipped blocked head: " + obj.get("name").getAsString() + "(" + id + ")");
                        continue;
                    }
                }

                result.add(new Head(
                        id,
                        Utils.validateUniqueId(obj.get("uuid").getAsString()).orElse(UUID.randomUUID()),
                        obj.get("name").getAsString(),
                        obj.get("value").getAsString(),
                        obj.get("tags").getAsString(),
                        response.date(),
                        category
                ));
            }

            return result;
        } catch (IOException ex) {
            HeadDB.getInstance().getLog().debug("Failed to load from provider: " + provider.name());
            if (HeadDB.getInstance().getConfig().getBoolean("fallback") && provider != HeadProvider.HEAD_ARCHIVE) { // prevent recursion. Maybe switch to an attempts counter down in the future
                provider = HeadProvider.HEAD_ARCHIVE;
                return fetchAndResolve(category);
            } else {
                HeadDB.getInstance().getLog().error("Could not fetch heads from any provider!");
                return new ArrayList<>();
            }
        }
    }

    public Response fetch(Category category) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(provider.getFormattedUrl(category)).openConnection();
        connection.setConnectTimeout(5000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", plugin.getName() + "/" + Utils.getVersion().orElse(plugin.getDescription().getVersion()));
        connection.setRequestProperty("Accept", "application/json");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            connection.disconnect();
            return new Response(builder.toString(), connection.getResponseCode(), connection.getHeaderField("date"));
        }
    }

    public HeadProvider getProvider() {
        return provider;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

}
