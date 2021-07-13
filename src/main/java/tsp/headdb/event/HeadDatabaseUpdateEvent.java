package tsp.headdb.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import tsp.headdb.database.HeadDatabase;

public class HeadDatabaseUpdateEvent extends Event implements Cancellable {

    private final HandlerList handlerList = new HandlerList();
    private boolean cancelled;
    private final HeadDatabase database;

    public HeadDatabaseUpdateEvent(HeadDatabase database) {
        this.database = database;
    }

    public HeadDatabase getDatabase() {
        return database;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

}
