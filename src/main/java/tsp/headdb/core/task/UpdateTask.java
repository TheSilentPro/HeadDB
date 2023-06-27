package tsp.headdb.core.task;

import org.bukkit.Bukkit;
import tsp.headdb.HeadDB;
import tsp.headdb.core.api.HeadAPI;
import tsp.headdb.core.api.events.AsyncHeadsFetchedEvent;
import tsp.headdb.implementation.head.Head;
import tsp.nexuslib.task.Task;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class UpdateTask implements Task {

    private final long interval;

    public UpdateTask(long interval) {
        this.interval = interval;
    }

    @Override
    public void run() {
        HeadAPI.getDatabase().update((time, heads) -> {
            int size = 0;
            for (List<Head> list : heads.values()) {
                for (Head ignored : list) {
                    size++;
                }
            }

            String providerName = HeadAPI.getDatabase().getRequester().getProvider().name();

            HeadDB.getInstance().getLog().debug("Fetched: " + size + " Heads | Provider: " + providerName + " | Time: " + time + "ms (" + TimeUnit.MILLISECONDS.toSeconds(time) + "s)");
            Bukkit.getPluginManager().callEvent(
                    new AsyncHeadsFetchedEvent(
                            size,
                            providerName,
                            time));
        });
        HeadDB.getInstance().getStorage().getPlayerStorage().backup();
        HeadDB.getInstance().getLog().debug("UpdateTask finished!");
    }

    @Override
    public long getRepeatInterval() {
        return interval;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

}
