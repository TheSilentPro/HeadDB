package com.github.thesilentpro.headdb.core.economy;

import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * An interface for interacting with an asynchronous economy system.
 */
public interface EconomyProvider {

    /**
     * Initializes the economy provider.
     * This may include hooking into other plugins or setting up internal state.
     */
    void init();

    /**
     * Checks asynchronously if the player can afford the specified amount.
     *
     * @param player the player whose balance to check (must not be null)
     * @param amount the amount to check
     * @return a CompletableFuture resolving to true if the player can afford the amount
     */
    CompletableFuture<Boolean> canAfford(Player player, double amount);

    /**
     * Attempts to withdraw the specified amount from the player's balance asynchronously.
     *
     * @param player the player to withdraw from (must not be null)
     * @param amount the amount to withdraw
     * @return a CompletableFuture resolving to true if the withdrawal succeeded
     */
    CompletableFuture<Boolean> withdraw(Player player, double amount);

    /**
     * Attempts to deposit the specified amount to the player's balance asynchronously.
     *
     * @param player the player to deposit to (must not be null)
     * @param amount the amount to deposit
     * @return a CompletableFuture resolving to true if the deposit succeeded
     */
    CompletableFuture<Boolean> deposit(Player player, double amount);

    /**
     * Attempts to purchase a by first checking if the player can afford it, then withdrawing the cost if they can.
     *
     * @param player the player making the purchase (must not be null)
     * @param cost the cost of the item or service
     * @return a CompletableFuture resolving to true if the purchase was successful
     */
    default CompletableFuture<Boolean> purchase(Player player, double cost) {
        Objects.requireNonNull(player, "Player must not be null!");

        return canAfford(player, cost).thenCompose(afforded -> {
            if (!afforded) {
                return CompletableFuture.completedFuture(false);
            }
            return withdraw(player, cost);
        });
    }

}
