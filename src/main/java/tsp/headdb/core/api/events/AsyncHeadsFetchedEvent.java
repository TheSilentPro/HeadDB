package tsp.headdb.core.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import tsp.headdb.implementation.category.Category;
import tsp.headdb.implementation.head.Head;

import java.util.List;
import java.util.Map;

public class AsyncHeadsFetchedEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Map<Category, List<Head>> heads;
    private final String providerName;
    private final long timeTook;

    public AsyncHeadsFetchedEvent(Map<Category, List<Head>> heads, String providerName, long timeTook) {
        super(true);
        this.heads = heads;
        this.providerName = providerName;
        this.timeTook = timeTook;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public Map<Category, List<Head>> getHeads() {
        return heads;
    }

    public String getProviderName() {
        return providerName;
    }

    public long getTimeTook() {
        return timeTook;
    }
}
