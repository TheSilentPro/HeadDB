package com.github.thesilentpro.headdb.implementation;


import com.github.thesilentpro.headdb.api.HeadAPI;
import com.github.thesilentpro.headdb.api.HeadDatabase;
import com.github.thesilentpro.headdb.api.model.Head;
import com.github.thesilentpro.headdb.core.util.Compatibility;
import com.github.thesilentpro.headdb.core.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link HeadAPI} using BaseHeadDatabase.
 */
public class BaseHeadAPI implements HeadAPI {

    private final ExecutorService executor;
    private final HeadDatabase database;

    public BaseHeadAPI(int workerThreads, HeadDatabase headDatabase) {
        this.executor = Utils.executorService(workerThreads, "HeadAPI Worker");
        this.database = headDatabase;
        this.database.update();
    }

    @Override
    public void awaitReady() {
        database.awaitReady();
    }

    @Override
    public boolean isReady() {
        return database.isReady();
    }

    @Override
    public CompletableFuture<List<Head>> onReady() {
        return database.onReady();
    }

    @NotNull
    @Override
    public CompletableFuture<List<Head>> searchByName(@NotNull String name, boolean lenient) {
        return getHeads().thenApplyAsync(heads ->
                heads.stream()
                        .filter(h -> lenient ? Utils.matches(h.getName(), name) : h.getName().equalsIgnoreCase(name))
                        .collect(Collectors.toList()), executor);
    }

    @NotNull
    @Override
    public CompletableFuture<Optional<Head>> findByName(@NotNull String name, boolean lenient) {
        return getHeads().thenApplyAsync(heads -> heads.stream()
                        .filter(h -> lenient ? Utils.matches(h.getName(), name) : h.getName().equalsIgnoreCase(name))
                        .findAny(), executor);
    }

    @NotNull
    @Override
    public CompletableFuture<Optional<Head>> findById(int id) {
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(database.getById(id)), executor);
    }

    @NotNull
    @Override
    public CompletableFuture<Optional<Head>> findByTexture(@NotNull String texture) {
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(database.getByTexture(texture)), executor);
    }

    @NotNull
    @Override
    public CompletableFuture<List<Head>> findByCategory(@NotNull String category) {
        return CompletableFuture.supplyAsync(() -> database.getByCategory(category), executor);
    }

    @NotNull
    @Override
    public CompletableFuture<List<Head>> findByTags(@NotNull String... tags) {
        return CompletableFuture.supplyAsync(() -> database.getByTags(tags), executor);
    }

    @NotNull
    @Override
    public CompletableFuture<List<Head>> getHeads() {
        return CompletableFuture.supplyAsync(() -> database.getHeads() != null ? database.getHeads() : Collections.emptyList(), executor);
    }

    @NotNull
    @Override
    public List<String> findKnownCategories() {
        if (database.getHeads() == null) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();
        for (Head head : database.getHeads()) {
            if (!result.contains(head.getCategory())) {
                result.add(head.getCategory());
            }
        }
        return result;
    }

    @NotNull
    @Override
    public List<ItemStack> computeLocalHeads() {
        OfflinePlayer[] players = Bukkit.getOfflinePlayers();
        List<ItemStack> heads = new ArrayList<>();
        for (OfflinePlayer player : players) {
            heads.add(Compatibility.asItem(player));
        }
        return heads;
    }

    @NotNull
    @Override
    public Optional<ItemStack> computeLocalHead(UUID uniqueId) {
        return Optional.of(Compatibility.asItem(Bukkit.getOfflinePlayer(uniqueId)));
    }

    /*
    @Override
    public CompletableFuture<Set<Head>> findFavoriteHeads(@NotNull UUID playerId, boolean withDate) {
        return CompletableFuture.supplyAsync(() ->
                HeadDB.getInstance().getPlayerDatabase().getOrCreate(playerId)
                        .getFavorites().stream()
                        .map(id -> findHeadById(id).join()
                                .orElseGet(() -> withDate && HeadDB.getInstance().getCfg().isLocalHeadsEnabled()
                                        ? computeLocalHead(playerId, true).join().orElse(null)
                                        : null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()), executor);
    }
    */

    @Override
    public @NotNull ExecutorService getExecutor() {
        return executor;
    }

}