package com.github.thesilentpro.headdb.core.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerDAO.class);

    public void createTable() {
        try (Connection conn = PlayerStorage.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(SqlUtils.CREATE_TABLE);
        } catch (SQLException ex) {
            LOGGER.error("Failed to create table", ex);
        }
    }

    public void saveAllPlayers(Map<UUID, PlayerData> dataMap) {
        try (Connection conn = PlayerStorage.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SqlUtils.INSERT_OR_REPLACE)) {

            for (PlayerData data : dataMap.values()) {
                stmt.setString(1, data.getUniqueId().toString());
                stmt.setString(2, data.getLanguage());

                String favorites = data.getFavorites() == null ? "" :
                        data.getFavorites().stream().map(String::valueOf).collect(Collectors.joining(","));
                stmt.setString(3, favorites);

                String localFavs = data.getLocalFavorites() == null ? "" :
                        data.getLocalFavorites().stream().map(UUID::toString).collect(Collectors.joining(","));

                stmt.setString(4, localFavs);
                stmt.setInt(5, data.isSoundEnabled() ? 1 : 0);

                stmt.addBatch();
            }

            stmt.executeBatch();

        } catch (SQLException ex) {
            LOGGER.error("Failed to save players", ex);
        }
    }

    public Map<UUID, PlayerData> loadAllPlayers() {
        Map<UUID, PlayerData> dataMap = new HashMap<>();

        try (Connection conn = PlayerStorage.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SqlUtils.SELECT_ALL)) {

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String lang = rs.getString("language");
                String favs = rs.getString("favorites");
                boolean sound = rs.getInt("sound_enabled") == 1;
                List<Integer> favorites = favs == null || favs.isEmpty()
                        ? new ArrayList<>()
                        : new ArrayList<>(Arrays.stream(favs.split(","))
                        .map(Integer::parseInt)
                        .toList());
                String localFavs = rs.getString("local_favorites");
                List<UUID> localFavorites = localFavs == null || localFavs.isEmpty()
                        ? new ArrayList<>()
                        : new ArrayList<>(Arrays.stream(localFavs.split(","))
                        .map(UUID::fromString)
                        .toList());

                PlayerData data = new PlayerData(uuid, lang, sound, favorites, localFavorites);

                dataMap.put(uuid, data);
            }

        } catch (SQLException ex) {
            LOGGER.error("Failed to load players", ex);
        }

        return dataMap;
    }
}
