package tsp.headdb.core.util;

import java.util.Optional;
import java.util.UUID;

public class Utils {

    public static Optional<UUID> validateUniqueId(String raw) {
        try {
            return Optional.of(UUID.fromString(raw));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

}
