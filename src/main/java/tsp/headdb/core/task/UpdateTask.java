package tsp.headdb.core.task;

import org.bukkit.Bukkit;
import tsp.headdb.HeadDB;
import tsp.headdb.core.api.HeadAPI;
import tsp.headdb.core.api.events.AsyncHeadsFetchedEvent;
import tsp.headdb.implementation.category.Category;
import tsp.headdb.implementation.head.Head;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UpdateTask implements Runnable {

    @Override
    public void run() {
        HeadAPI.getDatabase().update().thenAcceptAsync(result -> {
            HeadDB instance = HeadDB.getInstance();
            String providerName = HeadAPI.getDatabase().getRequester().getProvider().name();

            instance.getLog().debug("Fetched: " + getHeadsCount(result.heads()) + " Heads | Provider: " + providerName + " | Time: " + result.elapsed() + "ms (" + TimeUnit.MILLISECONDS.toSeconds(result.elapsed()) + "s)");
            Bukkit.getPluginManager().callEvent(
                    new AsyncHeadsFetchedEvent(
                            result.heads(),
                            providerName,
                            result.elapsed()));

            instance.getStorage().getPlayerStorage().backup();
            instance.getUpdateTask().ifPresentOrElse(task -> {
                instance.getLog().debug("UpdateTask completed! Times ran: " + task.getTimesRan());
            }, () -> instance.getLog().debug("Initial UpdateTask completed!"));
        });
    }

    private int getHeadsCount(Map<Category, List<Head>> heads) {
        int n = 0;
        for (List<Head> list : heads.values()) {
            for (int i = 0; i < list.size(); i++) {
                n++;
            }
        }

        return n;
    }

}
