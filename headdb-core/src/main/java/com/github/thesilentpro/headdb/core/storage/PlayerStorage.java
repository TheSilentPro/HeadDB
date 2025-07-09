package com.github.thesilentpro.headdb.core.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class PlayerStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerStorage.class);
    private static final String DB_URL = "jdbc:sqlite:plugins/HeadDB/data/data.db";

    private final Map<UUID, PlayerData> data = new HashMap<>();

    private final PlayerDAO playerDao;

    public PlayerStorage() {
        ensureDirectoryExists();
        this.playerDao = new PlayerDAO();
        this.playerDao.createTable();
    }

    public PlayerData getPlayer(UUID id) {
        return this.data.computeIfAbsent(id, i -> new PlayerData(i, "en", true, new ArrayList<>(), new ArrayList<>()));
    }

    public void load() {
        LOGGER.debug("Loading player and category data...");
        long start = System.currentTimeMillis();
        this.data.putAll(this.playerDao.loadAllPlayers());
        LOGGER.debug("Loaded all data in {}ms", System.currentTimeMillis() - start);
    }

    public void save() {
        LOGGER.debug("Saving player and category data...");
        long start = System.currentTimeMillis();
        this.playerDao.saveAllPlayers(this.data);
        LOGGER.debug("Saved all data in {}ms", System.currentTimeMillis() - start);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private static void ensureDirectoryExists() {
        File dbFile = new File("plugins/HeadDB/data/data.db");
        File parentDir = dbFile.getParentFile();

        if (!parentDir.exists()) {
            if (parentDir.mkdirs()) {
                LOGGER.debug("Created directory: {}", parentDir.getAbsolutePath());
            } else {
                LOGGER.error("Failed to create directory: {}", parentDir.getAbsolutePath());
            }
        }
    }
}