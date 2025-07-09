package com.github.thesilentpro.headdb.core.storage;

public class SqlUtils {

    public static final String CREATE_TABLE = """
        CREATE TABLE IF NOT EXISTS players (
            uuid TEXT PRIMARY KEY,
            language TEXT,
            favorites TEXT,
            local_favorites TEXT,
            sound_enabled INTEGER
        );
    """;

    public static final String INSERT_OR_REPLACE = """
        INSERT OR REPLACE INTO players (uuid, language, favorites, local_favorites, sound_enabled)
        VALUES (?, ?, ?, ?, ?);
    """;

    public static final String SELECT_ALL = """
        SELECT * FROM players;
    """;
}