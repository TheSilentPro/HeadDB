package tsp.headdb.core.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import tsp.headdb.HeadDB;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public class VaultProvider implements BasicEconomyProvider {

    private Economy economy;

    @Override
    public CompletableFuture<Boolean> canPurchase(Player player, BigDecimal cost) {
        double effectiveCost = cost.doubleValue();
        return CompletableFuture.supplyAsync(() -> economy.has(player, effectiveCost >= 0 ? effectiveCost : 0)); // Vault is really old...
    }

    @Override
    public CompletableFuture<Boolean> withdraw(Player player, BigDecimal amount) {
        double effectiveCost = amount.doubleValue();
        return CompletableFuture.supplyAsync(() -> economy.withdrawPlayer(player, effectiveCost >= 0 ? effectiveCost : 0).transactionSuccess());
    }


    @Override
    public void init() {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("Vault")) {
            HeadDB.getInstance().getLog().error("Vault is not installed!");
            return;
        }

        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            HeadDB.getInstance().getLog().error("Could not find vault economy provider!");
            return;
        }

        economy = economyProvider.getProvider();
    }

}
