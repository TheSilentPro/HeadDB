package tsp.headdb.api.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tsp.headdb.api.model.Category;
import tsp.headdb.api.model.Head;
import tsp.headdb.core.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author TheSilentPro (Silent)
 *
 * @deprecated Replaced by {@link HeadDataProvider}.
 */
@Deprecated(forRemoval = true, since = "5.0.0")
public class HeadStorageProvider implements HeadProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeadStorageProvider.class);

    @Override
    public String getUrl() {
        return "https://raw.githubusercontent.com/TheSilentPro/HeadStorage/master/storage/%s.json";
    }

    public CompletableFuture<ProviderResponse> fetchHeads(Category category, ExecutorService executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Head> heads = new ArrayList<>();

                HttpURLConnection connection = (HttpURLConnection) URI.create(String.format(getUrl(), category.getName())).toURL().openConnection();
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
                        heads.add(new Head(
                                obj.get("id").getAsInt(),
                                obj.get("name").getAsString(),
                                obj.get("value").getAsString(),
                                category.getName(),
                                null,
                                obj.get("tags").getAsString().split(","),
                                null,
                                null
                        ));
                    }
                }

                return new ProviderResponse(heads, Date.from(Instant.now()));
            } catch (IOException ex) {
                LOGGER.error("Failed to fetch heads for category: {}", category, ex);
                return new ProviderResponse(new ArrayList<>(), Date.from(Instant.now()));
            }
        }, executor);
    }

    @Override
    public CompletableFuture<ProviderResponse> fetchHeads(ExecutorService executor) {
        return CompletableFuture.supplyAsync(() -> {
            List<Head> heads = new ArrayList<>();
            for (Category category : Category.VALUES) {
                heads.addAll(fetchHeads(category, executor).join().heads());
            }
            return new ProviderResponse(heads, Date.from(Instant.now()));
        });
    }

}