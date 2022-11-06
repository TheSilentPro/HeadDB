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
import java.util.function.Consumer;

public class Requester {

    private final JavaPlugin plugin;
    private HeadProvider provider;

    public Requester(JavaPlugin plugin, HeadProvider provider) {
        this.plugin = plugin;
        this.provider = provider;
    }

    public void fetchAndResolve(Category category, Consumer<List<Head>> heads) {
        try {
            fetch(category, response -> {
                List<Head> result = new ArrayList<>();
                if (response.code() != 200) {
                    heads.accept(result);
                    return;
                }

                JsonArray main = JsonParser.parseString(response.response()).getAsJsonArray();
                for (JsonElement entry : main) {
                    JsonObject obj = entry.getAsJsonObject();
                    result.add(new Head(
                            obj.get("id").getAsInt(),
                            Utils.validateUniqueId(obj.get("uuid").getAsString()).orElse(UUID.randomUUID()),
                            obj.get("name").getAsString(),
                            obj.get("value").getAsString(),
                            obj.get("tags").getAsString(),
                            response.date()
                    ));
                }

                heads.accept(result);
            });
        } catch (IOException ex) {
            HeadDB.getInstance().getLog().debug("Failed to load from provider: " + provider.name());
            provider = HeadProvider.HEAD_ARCHIVE;
            fetchAndResolve(category, heads);
        }
    }

    public void fetch(Category category, Consumer<Response> response) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(provider.getFormattedUrl(category)).openConnection();
        connection.setConnectTimeout(5000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", plugin.getName() + "/" + plugin.getDescription().getVersion());
        connection.setRequestProperty("Accept", "application/json");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            response.accept(new Response(builder.toString(), connection.getResponseCode(), connection.getHeaderField("date")));
        }

        connection.disconnect();
    }

    public HeadProvider getProvider() {
        return provider;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

}
