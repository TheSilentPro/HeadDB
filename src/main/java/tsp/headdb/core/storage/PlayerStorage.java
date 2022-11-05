package tsp.headdb.core.storage;

import tsp.headdb.HeadDB;
import tsp.headdb.core.util.Utils;
import tsp.warehouse.storage.sql.SQLiteDataManager;

import java.io.File;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStorage extends SQLiteDataManager<Collection<PlayerData>> {

    private final ConcurrentHashMap<UUID, PlayerData> players = new ConcurrentHashMap<>();

    public PlayerStorage() {
        super(new File(HeadDB.getInstance().getDataFolder(), "player_data.db"), null);
        sendPreparedUpdate("CREATE TABLE IF NOT EXISTS data(uuid NOT NULL PRIMARY KEY VARCHAR(36), favorites TEXT)");
    }

    public void register(PlayerData player) {
        players.put(player.uniqueId(), player);
    }

    public Map<UUID, PlayerData> getPlayers() {
        return players;
    }

    @Override
    public CompletableFuture<Collection<PlayerData>> load() {
        return sendPreparedQuery("SELECT * FROM data").thenApply(set -> {
            Collection<PlayerData> data = new HashSet<>();
            try {
                while (set.next()) {
                    String favorites = set.getString("favorites");
                    Utils.validateUniqueId(set.getString("uuid")).ifPresentOrElse(uuid -> data.add(new PlayerData(uuid, favorites)), () -> HeadDB.getInstance().getLog().debug("Invalid uuid format!"));
                }

                return data;
            } catch (SQLException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> save(Collection<PlayerData> data) {
        if (data.size() <= 0) {
            return CompletableFuture.completedFuture(true); // Nothing to save
        }

        StringBuilder builder = new StringBuilder();
        for (PlayerData entry : data) {
            builder.append(String.format("(%s, %s),", entry.uniqueId().toString(), entry.favorites()));
        }
        return sendPreparedUpdate("INSERT OR REPLACE INTO data VALUES" + builder.substring(0, builder.length() - 1) + ";").thenApply(r -> true);
    }

}
