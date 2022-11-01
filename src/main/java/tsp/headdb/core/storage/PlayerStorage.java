package tsp.headdb.core.storage;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import tsp.headdb.HeadDB;
import tsp.warehouse.storage.DataManager;
import tsp.warehouse.storage.sql.SQLiteDataManager;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class PlayerStorage extends SQLiteDataManager<PlayerData> {

    public PlayerStorage() {
        super(new File(HeadDB.getInstance().getDataFolder(), "player_data.db"), null);
        sendPreparedUpdate("CREATE TABLE IF NOT EXISTS data(uuid NOT NULL PRIMARY KEY VARCHAR(36), name TEXT, favorites TEXT)");
    }

    @Override
    public CompletableFuture<Collection<PlayerData>> load() {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> save(Collection<PlayerData> data) {
        Bukkit.getScheduler().runTaskAsynchronously(HeadDB.getInstance(), () -> {
            StringBuilder builder = new StringBuilder();
            for (PlayerData entry : data) {
                builder.append(String.format("(%s, %s, %s),", entry.uniqueId().toString(), entry.name(), entry.favorites().toString()));
            }
            sendPreparedUpdate("INSERT OR REPLACE INTO data VALUES" + builder.substring(0, builder.length() - 1) + ";").join();
        });
        return CompletableFuture.completedFuture(true);
    }

}
