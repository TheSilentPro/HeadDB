package tsp.headdb.core.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * @author TheSilentPro (Silent)
 */
public class VaultProvider implements EconomyProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(VaultProvider.class);
    private Economy economy;

    @Override
    public void init() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            LOGGER.error("Vault is not installed but is enabled in the config.yml!");
            return;
        }

        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider == null) {
            LOGGER.error("Could not find vault economy provider!");
            return;
        }

        this.economy = economyProvider.getProvider();
    }

    @Override
    public CompletableFuture<Boolean> canAfford(Player player, double amount) {
        return CompletableFuture.supplyAsync(() -> economy.has(player, Math.max(0, amount)));
    }

    @Override
    public CompletableFuture<Boolean> withdraw(Player player, double amount) {
        return CompletableFuture.supplyAsync(() -> economy.withdrawPlayer(player, Math.max(0, amount)).transactionSuccess());
    }

}