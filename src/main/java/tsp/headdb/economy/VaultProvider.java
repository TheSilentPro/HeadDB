package tsp.headdb.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import tsp.headdb.util.Log;

import java.math.BigDecimal;

public class VaultProvider implements HEconomyProvider {

    private Economy economy;

    @Override
    public boolean canPurchase(Player player, BigDecimal cost) {
        return economy.has(player, cost.doubleValue());
    }

    @Override
    public void charge(Player player, BigDecimal amount) {
        economy.withdrawPlayer(player, amount.doubleValue());
    }

    public void initProvider() {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("Vault")) {
            Log.error("Vault is not installed!");
            return;
        }

        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            Log.error("Could not find vault economy provider!");
            return;
        }

        economy = economyProvider.getProvider();
    }

    public Economy getProvider() {
        return economy;
    }

}
