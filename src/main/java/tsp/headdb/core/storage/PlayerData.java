package tsp.headdb.core.storage;

import java.util.Collection;
import java.util.UUID;

public record PlayerData(UUID uniqueId, String name, Collection<String> favorites) {}