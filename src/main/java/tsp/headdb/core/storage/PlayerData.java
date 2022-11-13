package tsp.headdb.core.storage;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

// Notice that there is no need to add any additional boilerplate in order to make this serializable.
// Specifically, there is no need to add a serialVersionUID field,
// since the serialVersionUID of a record class is 0L unless explicitly declared,
// and the requirement for matching the serialVersionUID value is waived for record classes.
// Source: https://docs.oracle.com/en/java/javase/15/serializable-records/index.html#:~:text=Specifically%2C%20there%20is%20no%20need,is%20waived%20for%20record%20classes.
public record PlayerData(UUID uniqueId, Set<String> favorites) implements Serializable {}