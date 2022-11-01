package tsp.headdb.core.task;

import tsp.headdb.core.api.HeadAPI;
import tsp.smartplugin.tasker.Task;

@SuppressWarnings("ClassCanBeRecord")
public class DatabaseUpdateTask implements Task {

    private final long interval;

    public DatabaseUpdateTask(long interval) {
        this.interval = interval;
    }

    @Override
    public void run() {
        HeadAPI.getDatabase().update();
    }

    @Override
    public long getRepeatInterval() {
        return interval;
    }

}
