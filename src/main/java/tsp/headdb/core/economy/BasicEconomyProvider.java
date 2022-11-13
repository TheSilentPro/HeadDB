package tsp.headdb.core.economy;

import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public interface BasicEconomyProvider {

    CompletableFuture<Boolean> canPurchase(Player player, BigDecimal cost);

    CompletableFuture<Boolean> withdraw(Player player, BigDecimal amount);

    default CompletableFuture<Boolean> purchase(Player player, BigDecimal amount) {
        return canPurchase(player, amount).thenCompose(result -> result ? withdraw(player, amount) : CompletableFuture.completedFuture(false));
    }

    void init();

}
