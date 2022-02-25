package tsp.headdb.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import tsp.headdb.implementation.Head;
import tsp.headdb.implementation.Category;
import tsp.headdb.implementation.HeadDatabase;

import java.util.List;
import java.util.Map;

/**
 * This event is called AFTER a {@link HeadDatabase} updates.
 * The event is called asynchronously and can not be cancelled.
 *
 * @author TheSilentPro
 */
public class DatabaseUpdateEvent extends Event {

    private final HandlerList handlerList = new HandlerList();
    private final HeadDatabase database;
    private final Map<Category, List<Head>> heads;

    public DatabaseUpdateEvent(HeadDatabase database, Map<Category, List<Head>> heads) {
        super(true);

        this.database = database;
        this.heads = heads;
    }

    /**
     * Retrieve the {@link HeadDatabase} associated with this event
     *
     * @return The database
     */
    public HeadDatabase getDatabase() {
        return database;
    }

    /**
     * Retrieve the result of the update
     *
     * @return The heads fetched from the update
     */
    public Map<Category, List<Head>> getHeads() {
        return heads;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}