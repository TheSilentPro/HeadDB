package tsp.headdb.core.storage;

import tsp.headdb.HeadDB;
import tsp.headdb.core.api.HeadAPI;

import java.util.stream.Collectors;

public final class Storage {

    private final HeadDB instance = HeadDB.getInstance();
    private final PlayerStorage playerStorage = new PlayerStorage();
    private final HeadStorage headStorage = new HeadStorage();

    public void load() {
        instance.getLog().info("Loading data...");
        playerStorage.load().whenComplete((result, ex) -> {
            for (PlayerData player : result) {
                playerStorage.register(player);
            }
            instance.getLog().debug("Loaded " + result.size() + " player data!");
        });
        headStorage.load().whenComplete((result, ex) -> instance.getLog().debug("Loaded " + result.size() + " heads!"));
    }

    public void save() {
        playerStorage.save(playerStorage.getPlayers().values()).join();
        headStorage.save(HeadAPI.getHeads().collect(Collectors.toList())).join();
    }

    public PlayerStorage getPlayerStorage() {
        return playerStorage;
    }

    public HeadStorage getHeadStorage() {
        return headStorage;
    }

}
