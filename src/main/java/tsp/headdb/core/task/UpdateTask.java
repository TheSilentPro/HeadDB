package tsp.headdb.core.task;

import tsp.headdb.HeadDB;
import tsp.headdb.core.api.HeadAPI;
import tsp.smartplugin.tasker.Task;

@SuppressWarnings("ClassCanBeRecord")
public class UpdateTask implements Task {

    private final long interval;

    public UpdateTask(long interval) {
        this.interval = interval;
    }

    @Override
    public void run() {
        HeadAPI.getDatabase().update();
        //HeadDB.getInstance().getStorage().save();
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
