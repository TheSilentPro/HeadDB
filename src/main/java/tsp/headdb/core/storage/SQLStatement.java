package tsp.headdb.core.storage;

import tsp.headdb.core.player.PlayerData;

import java.io.IOException;
import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author TheSilentPro (Silent)
 */
public enum SQLStatement {

    TABLES_CREATE(
            """
            CREATE TABLE IF NOT EXISTS hdb_players (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                uuid UUID UNIQUE NOT NULL,
                lang VARCHAR(8),
                soundEnabled BOOLEAN,
                favorites TEXT
            );
            CREATE TABLE IF NOT EXISTS hdb_heads (
                id INTEGER UNIQUE PRIMARY KEY NOT NULL,            -- numeric id of the head
                texture VARCHAR(255),                              -- texture
                unique_id UUID NOT NULL,                           -- Unique UUID of the head
                name VARCHAR(60) NOT NULL,                         -- Name of the head
                tags VARCHAR(255),                                 -- Tags for the head (optional)
                category VARCHAR(255)                              -- Category of the head (optional)
            );
           """
    ),

    SELECT_PLAYERS("SELECT * FROM hdb_players;"),
    INSERT_PLAYER(
            """
             INSERT INTO hdb_players(id, uuid, lang, soundEnabled, favorites)
             VALUES(?, ?, ?, ?, ?)
             ON CONFLICT(uuid) DO UPDATE SET
                id = COALESCE(EXCLUDED.id, hdb_players.id),
                lang = COALESCE(EXCLUDED.lang, hdb_players.lang),
                soundEnabled = COALESCE(EXCLUDED.soundEnabled, hdb_players.soundEnabled),
                favorites = COALESCE(EXCLUDED.favorites, hdb_players.favorites);
            """,
            Types.VARCHAR, Types.VARCHAR, Types.BOOLEAN, Types.VARCHAR
    );

    private final String statement;
    private final int[] types;

    SQLStatement(String statement, int... types) {
        this.statement = statement;
        this.types = types;
    }

    SQLStatement(String statement) {
        this(statement, (int[]) null);
    }

    public int executeUpdate(Connection connection) throws SQLException, IOException {
        return connection.createStatement().executeUpdate(statement);
    }

    public int executePreparedUpdate(Connection connection, Object... parameters) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            if (parameters.length != types.length) {
                throw new IllegalArgumentException("Number of parameters does not match the number of placeholders.");
            }

            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i], types[i]);
            }

            return preparedStatement.executeUpdate();
        }
    }

    public ResultSet executeQuery(Connection connection) throws SQLException {
        return connection.createStatement().executeQuery(statement);
    }

    public int executePreparedBatch(Connection connection, Map<UUID, PlayerData> players) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            for (Map.Entry<UUID, PlayerData> entry : players.entrySet()) {
                PlayerData player = entry.getValue();

                preparedStatement.setInt(1, entry.getValue().getId());
                preparedStatement.setString(2, entry.getKey().toString());
                preparedStatement.setString(3, player.getLang());
                preparedStatement.setBoolean(4, player.isSoundEnabled());
                preparedStatement.setString(5, player.getFavorites().stream().map(String::valueOf).collect(Collectors.joining("|")));

                preparedStatement.addBatch();
            }

            int[] results = preparedStatement.executeBatch();
            return results.length;
        }
    }

}