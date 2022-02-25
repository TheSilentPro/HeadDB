package tsp.headdb.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import tsp.headdb.util.Log;
import tsp.headdb.util.Utils;

import java.math.BigDecimal;
import java.util.function.Consumer;

/**
 * A {@link BasicEconomyProvider} for Vault
 *
 * @author TheSilentPro
 * @since 4.0.0
 */
public class VaultProvider implements BasicEconomyProvider {

    private Economy economy;

    @Override
    public void canPurchase(Player player, BigDecimal cost, Consumer<Boolean> result) {
        Utils.async(t -> result.accept(economy.has(player, cost.doubleValue())));
    }

    @Override
    public void charge(Player player, BigDecimal amount, Consumer<Boolean> result) {
        Utils.async(t -> result.accept(economy.withdrawPlayer(player, amount.doubleValue()).transactionSuccess()));
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
