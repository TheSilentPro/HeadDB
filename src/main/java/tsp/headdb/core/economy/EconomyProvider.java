package tsp.headdb.core.economy;

import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

/**
 * @author TheSilentPro (Silent)
 */
public interface EconomyProvider {

    void init();

    CompletableFuture<Boolean> canAfford(Player player, double amount);

    CompletableFuture<Boolean> withdraw(Player player, double amount);

    default CompletableFuture<Boolean> purchase(Player player, double cost) {
        return canAfford(player, cost).thenCompose(afforded -> {
            if (!afforded) {
                return CompletableFuture.completedFuture(false);
            } else {
                return withdraw(player, cost);
            }
        });
    }

}