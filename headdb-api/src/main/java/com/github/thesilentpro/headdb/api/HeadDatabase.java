package com.github.thesilentpro.headdb.api;

import com.github.thesilentpro.headdb.api.model.Head;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface HeadDatabase {

    CompletableFuture<List<Head>> update();

    /**
     * Blocks until the most recent update() completes (success or failure),
     * then returns true if it succeeded, or false if it failed.
     */
    boolean awaitReady();

    /**
     * Non‑blocking check: has the most recent update() finished (successfully or not)?
     */
    boolean isReady();

    /**
     * Waits until the {@link HeadDatabase#update()} finishes before executing.
     */
    CompletableFuture<List<Head>> onReady();

    // Get all heads, or null if not yet loaded
    List<Head> getHeads();

    // Get heads by category (returns a list of heads in the given category)
    List<Head> getByCategory(String category);

    // Get heads by tag (returns a list of heads with the given tag)
    List<Head> getByTags(String... tags);

    // Get a head by ID (returns a head or null if not found)
    Head getById(int id);

    // Get a head by texture (returns a head or null if not found)
    Head getByTexture(String texture);

}