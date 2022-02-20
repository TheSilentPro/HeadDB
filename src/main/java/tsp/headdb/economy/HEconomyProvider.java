package tsp.headdb.economy;

import org.bukkit.entity.Player;

import java.math.BigDecimal;

public interface HEconomyProvider {

    default boolean canPurchase(Player player, BigDecimal cost) {
        return true;
    }

    default void charge(Player player, BigDecimal amount) {

    }

    default void initProvider() {

    }

}
