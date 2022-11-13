package tsp.headdb.core.storage;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Storage {

    private final Executor executor;
    private final PlayerStorage playerStorage;

    public Storage(int threads) {
        executor = Executors.newFixedThreadPool(threads, HeadDBThreadFactory.FACTORY);
        playerStorage = new PlayerStorage(this);
    }

    public PlayerStorage getPlayerStorage() {
        return playerStorage;
    }

    public Executor getExecutor() {
        return executor;
    }

}
