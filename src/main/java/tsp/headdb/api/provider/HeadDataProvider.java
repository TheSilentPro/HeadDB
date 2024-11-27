package tsp.headdb.api.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.api.model.Head;
import tsp.headdb.core.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author TheSilentPro (Silent)
 */
public class HeadDataProvider implements HeadProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeadDataProvider.class);

    @Override
    public String getUrl() {
        return "https://raw.githubusercontent.com/TheSilentPro/HeadData/refs/heads/main/heads.json";
    }

    @Override
    public CompletableFuture<ProviderResponse> fetchHeads(ExecutorService executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Head> heads = new ArrayList<>();

                HttpURLConnection connection = (HttpURLConnection) URI.create(getUrl()).toURL().openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestProperty("User-Agent", Utils.getUserAgent());
                connection.setRequestProperty("Accept", "application/json");

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    connection.disconnect();

                    JsonArray main = JsonParser.parseString(builder.toString()).getAsJsonArray();

                    for (JsonElement entry : main) {
                        JsonObject obj = entry.getAsJsonObject();

                        // Index for json arrays
                        int i = 0;

                        String[] tags = null;
                        if (!obj.get("tags").isJsonNull()) {
                            JsonArray rawTags = obj.get("tags").getAsJsonArray();
                            tags = new String[rawTags.size()];
                            for (JsonElement rawTag : rawTags) {
                                tags[i] = rawTag.getAsString();
                                i++;
                            }
                        }

                        String[] contributors = null;
                        if (!obj.get("contributors").isJsonNull()) {
                            i = 0;
                            JsonArray rawContributors = obj.get("contributors").getAsJsonArray();
                            contributors = new String[rawContributors.size()];
                            for (JsonElement rawContributor : rawContributors) {
                                contributors[i] = rawContributor.getAsString();
                                i++;
                            }
                        }

                        String[] collections = null;
                        if (!obj.get("collections").isJsonNull()) {
                            i = 0;
                            JsonArray rawCollections = obj.get("collections").getAsJsonArray();
                            collections = new String[rawCollections.size()];
                            for (JsonElement rawCollection : rawCollections) {
                                collections[i] = rawCollection.getAsString();
                                i++;
                            }
                        }

                        String date = obj.has("publish_date") && !obj.get("publish_date").isJsonNull() ? obj.get("publish_date").getAsString() : null;
                        heads.add(new Head(
                                obj.get("id").getAsInt(),
                                obj.get("name").getAsString(),
                                obj.has("texture") && !obj.get("texture").isJsonNull() ? obj.get("texture").getAsString() : null,
                                obj.has("category") && !obj.get("category").isJsonNull() ? obj.get("category").getAsString() : null,
                                date != null ? (date.substring(8, 10) + "-" + date.substring(5, 7) + "-" + date.substring(0, 4)) : null, // Parses and reverses the date format (yyyy-MM-dd -> dd-MM-yyyy)
                                tags,
                                contributors,
                                collections
                        ));
                    }
                }

                return new ProviderResponse(heads, Date.from(Instant.now()));
            } catch (Exception ex) {
                LOGGER.error("Failed to fetch heads!", ex);
                return new ProviderResponse(new ArrayList<>(), Date.from(Instant.now()));
            }
        }, executor);
    }

}