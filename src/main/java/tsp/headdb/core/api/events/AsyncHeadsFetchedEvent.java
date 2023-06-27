package tsp.headdb.core.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AsyncHeadsFetchedEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final int headsCount;
    private final String providerName;
    private final long timeTook;

    public AsyncHeadsFetchedEvent(int headsCount, String providerName, long timeTook) {
        super(true);
        this.headsCount = headsCount;
        this.providerName = providerName;
        this.timeTook = timeTook;
    }

    @NotNull
    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return new HandlerList();
    }

    @SuppressWarnings("unused")
    public int getHeadsCount() {
        return headsCount;
    }

    @SuppressWarnings("unused")
    public String getProviderName() {
        return providerName;
    }

    @SuppressWarnings("unused")
    public long getTimeTook() {
        return timeTook;
    }
}
