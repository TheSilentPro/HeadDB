package tsp.headdb.storage;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import tsp.headdb.HeadDB;
import tsp.headdb.util.Log;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Manages the data file that stores information
 */
public class PlayerDataFile {

    private final File file;
    private JsonObject main;

    public PlayerDataFile(String name) {
        HeadDB plugin = HeadDB.getInstance();
        // This check avoids warning in console
        if (plugin.getResource(name) != null && !new File(plugin.getDataFolder() + "/" + name).exists()) {
            plugin.saveResource(name, false);
        }

        this.file = new File(plugin.getDataFolder() + "/" + name);
    }

    @Nonnull
    public List<String> getFavoriteHeadsByTexture(UUID uuid) {
        List<String> result = new ArrayList<>();

        if (main.has(uuid.toString()) && main.get(uuid.toString()).getAsJsonObject().has("favorites")) {
            JsonArray favorites = main.get(uuid.toString()).getAsJsonObject().get("favorites").getAsJsonArray();
            for (int i = 0; i < favorites.size(); i++) {
                String str = favorites.get(i).toString();
                result.add(str.substring(1, str.length() - 1));
            }
        }

        return result;
    }

    public void modifyFavorite(UUID uuid, String textureValue, ModificationType modificationType) {
        JsonObject userObject;
        if (main.has(uuid.toString())) {
            userObject = main.get(uuid.toString()).getAsJsonObject();
        } else {
            userObject = new JsonObject();
        }

        JsonArray favorites;
        if (userObject.has("favorites")) {
            favorites = userObject.get("favorites").getAsJsonArray();
        } else {
            favorites = new JsonArray();
        }

        JsonPrimitive value = new JsonPrimitive(textureValue);
        if (modificationType == ModificationType.SET) {
            if (favorites.contains(value)) {
                // Head is already in the list, no need to modify it
                return;
            }

            favorites.add(value);
        } else if (modificationType == ModificationType.REMOVE) {
            if (!favorites.contains(value)) {
                // Head is not in the list, no need to modify it
                return;
            }

            favorites.remove(value);
        }

        userObject.add("favorites", favorites);
        main.add(uuid.toString(), userObject);
    }

    @Nullable
    public String getUsername(UUID uuid) {
        return main.get(uuid.toString()).getAsJsonObject().get("username").toString();
    }

    public Set<String> getEntries() {
        return main.keySet();
    }

    public void modifyUsername(UUID uuid, String username, ModificationType modificationType) {
        JsonObject userObject;
        if (main.has(uuid.toString())) {
            userObject = main.get(uuid.toString()).getAsJsonObject();
        } else {
            userObject = new JsonObject();
        }

        if (modificationType == ModificationType.SET) {
            userObject.addProperty("username", username);
        } else {
            userObject.remove("username");
        }
        main.add(uuid.toString(), userObject);
    }

    public void load() {
        try {
            main = new JsonParser().parse(new FileReader(file)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        if (main == null) {
            Log.debug("No data to save! Skipping...");
            return;
        }

        FileWriter writer;
        try {
            writer = new FileWriter(file);
            writer.write(main.toString());
            writer.close();
            Log.debug("Saved data to " + file.getName());
        } catch (IOException e) {
            Log.error("Failed to save player_data.json contents!");
            Log.error(e);
        }
    }

    public File getFile() {
        return file;
    }

    public enum ModificationType {
        SET,
        REMOVE;
    }

}
