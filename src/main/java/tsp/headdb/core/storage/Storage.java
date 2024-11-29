package tsp.headdb.core.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tsp.headdb.HeadDB;
import tsp.headdb.core.player.PlayerData;
import tsp.headdb.core.util.Utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

/**
 * @author TheSilentPro (Silent)
 */
public class Storage {

    private static final Logger LOGGER = LoggerFactory.getLogger(Storage.class);
    private final ExecutorService executor;
    private Connection connection;

    public Storage() {
        this.executor = Utils.from(HeadDB.getInstance().getCfg().getStorageWorkerThreads(), "HeadDB Storage Worker");
    }

    public Storage init() {
        createTables();
        return this;
    }

    public CompletableFuture<ResultSet> selectPlayers() {
        return connect().thenComposeAsync(conn -> {
            try {
                LOGGER.debug("Fetching players from database...");
                return CompletableFuture.completedFuture(SQLStatement.SELECT_PLAYERS.executeQuery(conn));
            } catch (SQLException ex) {
                LOGGER.error("Failed to select players!", ex);
                return CompletableFuture.failedFuture(ex);
            } finally {
                disconnect();
            }
        }, executor);
    }

    public CompletableFuture<Void> insertPlayer(UUID id, String lang, boolean soundEnabled, String favorites) {
        return connect().thenAcceptAsync(conn -> {
            try {
                int res = SQLStatement.INSERT_PLAYER.executeUpdate(conn);
                LOGGER.debug("[INSERT]: RESPONSE={} | ID={} | LANG={} | sound={} | FAVORITES={}", res, id, lang, soundEnabled, favorites.substring(0, 16));
            } catch (SQLException | IOException ex) {
                LOGGER.error("Failed to create tables!", ex);
            } finally {
                disconnect();
            }
        }, executor);
    }

    public CompletableFuture<Void> insertAllPlayers(Map<UUID, PlayerData> players) {
        return connect().thenAcceptAsync(conn -> {
            try {
                int inserted = SQLStatement.INSERT_PLAYER.executePreparedBatch(conn, players);
                LOGGER.debug("[INSERT | ALL]: IN={} | OUT={}", players.size(), inserted);
            } catch (SQLException ex) {
                LOGGER.error("Failed to insert all players!", ex);
            } finally {
                disconnect();
            }
        }, executor);
    }

    public CompletableFuture<Void> createTables() {
        return connect().thenAcceptAsync(conn -> {
            try {
                int res = SQLStatement.TABLES_CREATE.executeUpdate(conn);
                LOGGER.debug("[CREATE]: RES={}", res);
            } catch (SQLException | IOException ex) {
                LOGGER.error("Failed to create tables!", ex);
            } finally {
                disconnect();
            }
        }, executor);
    }

    public CompletableFuture<Connection> connect() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                this.connection = DriverManager.getConnection("jdbc:sqlite:" + HeadDB.getInstance().getDataFolder() + "/data.db");
                LOGGER.debug("SQL connection established!");
                return this.connection;
            } catch (SQLException ex) {
                LOGGER.error("Failed to connect to database!", ex);
                throw new CompletionException("Could not connect to database!", ex);
            }
        }, executor);
    }

    public CompletableFuture<Void> disconnect() {
        return CompletableFuture.runAsync(() -> {
            try {
                if (this.connection != null && !this.connection.isClosed()) {
                    this.connection.close();
                    LOGGER.debug("SQL connection terminated!");
                }
            } catch (SQLException ex) {
                LOGGER.error("Failed to close connection!", ex);
                throw new CompletionException("Could not close database connection", ex);
            }
        }, executor);
    }
}