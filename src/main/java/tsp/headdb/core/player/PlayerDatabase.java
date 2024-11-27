package tsp.headdb.core.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tsp.headdb.HeadDB;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author TheSilentPro (Silent)
 */
public class PlayerDatabase {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerDatabase.class);

    private static final Pattern FAVORITES_DELIMITER = Pattern.compile("\\|");
    private final ConcurrentHashMap<UUID, PlayerData> data = new ConcurrentHashMap<>();

    public PlayerData getOrCreate(UUID uuid) {
        PlayerData data = this.data.get(uuid);
        if (data == null) {
            data = new PlayerData(this.data
                    .values()
                    .stream()
                    .mapToInt(PlayerData::getId)
                    .max()
                    .orElse(0) + 1,
                    uuid,
                    "en",
                    true);
            this.data.put(uuid, data);
        }

        return data;
    }

    private PlayerData register(int id, UUID uuid, String lang, boolean enableSounds, int... favorites) {
        if (!this.data.containsKey(uuid)) {
            this.data.put(uuid, new PlayerData(id, uuid, lang, enableSounds, favorites));
        }
        return this.data.get(uuid);
    }

    public void save() {
        HeadDB.getInstance().getStorage().insertAllPlayers(data).whenComplete((result, ex) -> {
            if (ex != null) {
                LOGGER.error("Failed to save all players!", ex);
                return;
            }
            LOGGER.info("Successfully saved all players to the database.");
        });
    }

    public void load() {
        HeadDB.getInstance().getStorage().selectPlayers().whenComplete((resultSet, ex) -> {
            if (ex != null) {
                LOGGER.error("Failed to load players!", ex);
                return;
            }

            try {
                int count = 0;
                while (resultSet.next()) {
                    count++;
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    int[] favorites = Arrays.stream(FAVORITES_DELIMITER.split(resultSet.getString("favorites")))
                            .mapToInt(Integer::parseInt)
                            .toArray();
                    this.data.put(uuid, register(resultSet.getInt("id"), uuid, resultSet.getString("lang"), resultSet.getBoolean("soundEnabled"), favorites));
                }
                LOGGER.info("Loaded {} players!", count);
            } catch (SQLException sqlex) {
                LOGGER.error("Failed to iterate players!", sqlex);
            }
        });
    }

    public Map<UUID, PlayerData> getData() {
        return Collections.unmodifiableMap(data);
    }

}