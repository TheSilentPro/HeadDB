package tsp.headdb.core.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import tsp.headdb.HeadDB;
import tsp.helperlite.scheduler.promise.Promise;

import java.math.BigDecimal;

public class VaultProvider implements BasicEconomyProvider {

    private Economy economy;

    @Override
    public Promise<Boolean> canPurchase(Player player, BigDecimal cost) {
        double effectiveCost = cost.doubleValue();
        return Promise.supplyingAsync(() -> economy.has(player, effectiveCost >= 0 ? effectiveCost : 0));
    }

    @Override
    public Promise<Boolean> withdraw(Player player, BigDecimal amount) {
        double effectiveCost = amount.doubleValue();
        return Promise.supplyingAsync(() -> economy.withdrawPlayer(player, effectiveCost >= 0 ? effectiveCost : 0).transactionSuccess());
    }


    @Override
    public void init() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            HeadDB.getInstance().getLog().error("Vault is not installed!");
            return;
        }

        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider == null) {
            HeadDB.getInstance().getLog().error("Could not find vault economy provider!");
            return;
        }

        economy = economyProvider.getProvider();
    }

}
