package com.github.thesilentpro.headdb.api;

import com.github.thesilentpro.headdb.api.model.Head;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Public-facing API for head database operations.
 */
public interface HeadAPI {

    /**
     * Blocks until the database has finished its initial load.
     */
    void awaitReady();

    /**
     * Returns true if the database is fully loaded (success or failure).
     */
    boolean isReady();

    /**
     * Waits until the {@link HeadDatabase#update()} finishes before executing.
     */
    CompletableFuture<List<Head>> onReady();

    @NotNull
    CompletableFuture<List<Head>> searchByName(@NotNull String name, boolean lenient);

    @NotNull
    default CompletableFuture<List<Head>> searchByName(@NotNull String name) {
        return searchByName(name, true);
    }

    @NotNull
    CompletableFuture<Optional<Head>> findByName(@NotNull String name, boolean lenient);

    @NotNull
    default CompletableFuture<Optional<Head>> findByName(@NotNull String name) {
        return findByName(name, false);
    }

    @NotNull
    CompletableFuture<Optional<Head>> findById(int id);

    @NotNull
    CompletableFuture<Optional<Head>> findByTexture(@NotNull String texture);

    @NotNull
    CompletableFuture<List<Head>> findByCategory(@NotNull String category);

    @NotNull
    CompletableFuture<List<Head>> findByTags(@NotNull String... tags);

    @NotNull
    CompletableFuture<List<Head>> getHeads();

    @NotNull
    List<ItemStack> computeLocalHeads();

    @NotNull
    Optional<ItemStack> computeLocalHead(UUID uniqueId);

    @NotNull
    List<String> findKnownCategories();

    /**
     * Returns the underlying executor service.
     */
    @NotNull
    ExecutorService getExecutor();

}