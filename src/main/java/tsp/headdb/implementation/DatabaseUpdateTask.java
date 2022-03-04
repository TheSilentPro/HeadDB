package tsp.headdb.implementation;

import tsp.headdb.api.HeadAPI;
import tsp.headdb.util.Log;

/**
 * Task that updates the database on an interval
 */
public class DatabaseUpdateTask implements Runnable {

    @Override
    public void run() {
        HeadAPI.getDatabase().update(heads -> Log.info("Fetched " + HeadAPI.getHeads().size() + " heads!"));
    }

}
