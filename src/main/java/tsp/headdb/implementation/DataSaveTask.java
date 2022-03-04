package tsp.headdb.implementation;

import tsp.headdb.HeadDB;

public class DataSaveTask implements Runnable {

    @Override
    public void run() {
        HeadDB.getInstance().getPlayerData().save();
    }
}
