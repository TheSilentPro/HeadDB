package tsp.headdb.economy;

import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.function.Consumer;

/**
 * An interface for generalizing Economy Provider's
 *
 * @author TheSilentPro
 * @since 4.0.0
 * @see VaultProvider
 * @see TreasuryProvider
 */
public interface BasicEconomyProvider {

    /**
     * Retrieve if the player can purchase a head using this economy provider
     *
     * @param player The player
     * @param cost The cost
     * @param result If the player has enough to purchase
     */
    default void canPurchase(Player player, BigDecimal cost, Consumer<Boolean> result) {
        result.accept(true);
    }

    /**
     * Charge the player a specific amount using this economy provider
     *
     * @param player The player
     * @param amount The amount
     * @param result If the transaction was successful
     */
    default void charge(Player player, BigDecimal amount, Consumer<Boolean> result) {
        result.accept(true);
    }

    /**
     * Convenience method for initializing economy
     *
     * @see VaultProvider#initProvider()
     * @see TreasuryProvider#initProvider()
     */
    void initProvider();

}
