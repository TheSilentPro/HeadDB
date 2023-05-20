package tsp.headdb.core.storage;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public record PlayerData(UUID uniqueId, Set<String> favorites) implements Serializable {}