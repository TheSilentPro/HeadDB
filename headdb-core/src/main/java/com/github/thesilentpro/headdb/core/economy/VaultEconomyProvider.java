package com.github.thesilentpro.headdb.core.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * EconomyProvider implementation that uses Vault for economy operations.
 */
public class VaultEconomyProvider implements EconomyProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(VaultEconomyProvider.class);
    private Economy economy;

    @Override
    public void init() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            LOGGER.error("Vault is not installed but is enabled in the config.yml!");
            return;
        }

        RegisteredServiceProvider<Economy> provider = Bukkit.getServer()
                .getServicesManager()
                .getRegistration(Economy.class);

        if (provider == null) {
            LOGGER.error("Could not find a Vault economy provider!");
            return;
        }

        this.economy = provider.getProvider();
        LOGGER.info("Vault economy provider hooked: {}", economy.getName());
    }

    @Override
    public CompletableFuture<Boolean> canAfford(Player player, double amount) {
        Objects.requireNonNull(player, "Player cannot be null");
        if (economy == null) {
            return failed("Economy provider not initialized");
        }
        if (amount < 0) {
            return failed("Amount must be non-negative");
        }

        return CompletableFuture.supplyAsync(() -> economy.has(player, amount));
    }

    @Override
    public CompletableFuture<Boolean> withdraw(Player player, double amount) {
        Objects.requireNonNull(player, "Player cannot be null");
        if (economy == null) {
            return failed("Economy provider not initialized");
        }
        if (amount < 0) {
            return failed("Amount must be non-negative");
        }

        return CompletableFuture.supplyAsync(() -> economy.withdrawPlayer(player, amount).transactionSuccess());
    }

    @Override
    public CompletableFuture<Boolean> deposit(Player player, double amount) {
        Objects.requireNonNull(player, "Player cannot be null");
        if (economy == null) {
            return failed("Economy provider not initialized");
        }
        if (amount < 0) {
            return failed("Amount must be non-negative");
        }

        return CompletableFuture.supplyAsync(() -> economy.depositPlayer(player, amount).transactionSuccess());
    }

    private CompletableFuture<Boolean> failed(String message) {
        LOGGER.error(message);
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        future.completeExceptionally(new IllegalStateException(message));
        return future;
    }

}
