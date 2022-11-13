package tsp.headdb.core.task;

import tsp.headdb.HeadDB;
import tsp.headdb.core.api.HeadAPI;
import tsp.smartplugin.tasker.Task;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("ClassCanBeRecord")
public class UpdateTask implements Task {

    private final long interval;

    public UpdateTask(long interval) {
        this.interval = interval;
    }

    @Override
    public void run() {
        HeadAPI.getDatabase().update((time, heads) -> HeadDB.getInstance().getLog().debug("Fetched: " + heads.size() + " Heads | Provider: " + HeadAPI.getDatabase().getRequester().getProvider().name() + " | Time: " + time + "ms (" + TimeUnit.MILLISECONDS.toSeconds(time) + "s)"));
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
