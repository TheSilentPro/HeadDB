package tsp.headdb.api.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tsp.headdb.api.model.Head;
import tsp.headdb.core.util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
 * {@link HeadProvider} that will read from a json file.
 * The json format should be that of {@link HeadDataProvider}.
 *
 * @author TheSilentPro (Silent)
 */
public class HeadFileProvider implements HeadProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeadFileProvider.class);

    private final File file;

    public HeadFileProvider(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    /**
     * Reading is done from the file, not an url.
     *
     * @return Always null.
     */
    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public CompletableFuture<ProviderResponse> fetchHeads(ExecutorService executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Head> heads = new ArrayList<>();

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                    JsonArray main = JsonParser.parseString(builder.toString()).getAsJsonArray();

                    for (JsonElement entry : main) {
                        JsonObject obj = entry.getAsJsonObject();
                        JsonArray rawTags = obj.get("tags").getAsJsonArray();
                        String[] tags = new String[rawTags.size()];
                        int i = 0;
                        for (JsonElement rawTag : rawTags) {
                            tags[i] = rawTag.getAsString();
                            i++;
                        }

                        i = 0;
                        JsonArray rawContributors = obj.get("contributors").getAsJsonArray();
                        String[] contributors = new String[rawContributors.size()];
                        for (JsonElement rawContributor : rawContributors) {
                            contributors[i] = rawContributor.getAsString();
                            i++;
                        }

                        i = 0;
                        JsonArray rawCollections = obj.get("collections").getAsJsonArray();
                        String[] collections = new String[rawCollections.size()];
                        for (JsonElement rawCollection : rawCollections) {
                            collections[i] = rawCollection.getAsString();
                            i++;
                        }

                        heads.add(new Head(
                                obj.get("id").getAsInt(),
                                obj.get("name").getAsString(),
                                obj.get("texture").getAsString(),
                                obj.get("category").getAsString(),
                                obj.get("publish_date").getAsString(),
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