package tsp.headdb.core.storage;

import tsp.headdb.HeadDB;
import tsp.headdb.core.util.Utils;
import tsp.headdb.implementation.head.Head;
import tsp.warehouse.storage.sql.SQLiteDataManager;

import java.io.File;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class HeadStorage extends SQLiteDataManager<Collection<Head>> {

    public HeadStorage() {
        super(new File(HeadDB.getInstance().getDataFolder(), "head_data.db"), null);
        sendPreparedUpdate("CREATE TABLE IF NOT EXISTS data(id INTEGER, uuid VARCHAR(36), name TEXT, texture TEXT, tags TEXT, updated TEXT);");
    }

    @Override
    public CompletableFuture<Collection<Head>> load() {
        return sendPreparedQuery("SELECT * FROM data").thenApply(set -> {
            try {
                Collection<Head> data = new HashSet<>();
                while (set.next()) {
                    data.add(new Head(
                            set.getInt("id"),
                            Utils.validateUniqueId(set.getString("uuid")).orElse(UUID.randomUUID()),
                            set.getString("name"),
                            set.getString("texture"),
                            set.getString("tags"),
                            set.getString("updated")
                    ));
                }

                return data;
            } catch (SQLException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> save(Collection<Head> data) {
        if (data.size() <= 0) {
            return CompletableFuture.completedFuture(true); // Nothing to save
        }

        StringBuilder builder = new StringBuilder();
        for (Head head : data) {
            builder.append(String.format("(%d, %s, %s, %s, %s, %s),",
                    head.getId(),
                    head.getUniqueId().toString(),
                    head.getName(),
                    head.getTexture(),
                    head.getTags(),
                    head.getUpdated()
            ));
        }

        //noinspection StringOperationCanBeSimplified
        return sendPreparedUpdate("INSERT OR REPLACE INTO data VALUES" + builder.toString().substring(0, builder.length() - 1) + ";").thenApply(r -> true);
    }
    
}
