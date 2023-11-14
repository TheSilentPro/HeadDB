package tsp.headdb.core.economy;

import org.bukkit.entity.Player;
import tsp.helperlite.scheduler.promise.Promise;

import java.math.BigDecimal;

public interface BasicEconomyProvider {

    Promise<Boolean> canPurchase(Player player, BigDecimal cost);

    Promise<Boolean> withdraw(Player player, BigDecimal amount);

    default Promise<Boolean> purchase(Player player, BigDecimal amount) {
        return canPurchase(player, amount).thenComposeAsync(result -> result ? withdraw(player, amount) : Promise.completed(false));
    }

    void init();

}
