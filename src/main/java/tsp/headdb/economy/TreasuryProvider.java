package tsp.headdb.economy;

import me.lokka30.treasury.api.common.service.Service;
import me.lokka30.treasury.api.common.service.ServiceRegistry;
import me.lokka30.treasury.api.economy.EconomyProvider;
import me.lokka30.treasury.api.economy.account.PlayerAccount;
import me.lokka30.treasury.api.economy.currency.Currency;
import me.lokka30.treasury.api.economy.response.EconomyException;
import me.lokka30.treasury.api.economy.response.EconomySubscriber;
import me.lokka30.treasury.api.economy.transaction.EconomyTransactionInitiator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tsp.headdb.HeadDB;
import tsp.headdb.util.Log;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A {@link BasicEconomyProvider} for Treasury
 *
 * @author TheSilentPro
 * @since 4.0.0
 */
public class TreasuryProvider implements BasicEconomyProvider {

    private EconomyProvider provider;
    private EconomyTransactionInitiator<?> transactionInitiator;
    private Currency currency;

    @Override
    public void canPurchase(Player player, BigDecimal cost, Consumer<Boolean> result) {
        EconomySubscriber
                .<Boolean>asFuture(s -> provider.hasPlayerAccount(player.getUniqueId(), s))
                .thenCompose(val -> {
                    if (val) {
                        return EconomySubscriber.<PlayerAccount>asFuture(s -> provider.retrievePlayerAccount(player.getUniqueId(), s));
                    } else {
                        return EconomySubscriber.<PlayerAccount>asFuture(s -> provider.createPlayerAccount(player.getUniqueId(), s));
                    }
                })
                .thenCompose(account -> EconomySubscriber.<BigDecimal>asFuture(s -> account.retrieveBalance(currency, s)))
                .whenComplete((bal, ex) -> {
                    result.accept(bal.compareTo(cost) >= 0);
                });
    }

    @Override
    public void charge(Player player, BigDecimal amount, Consumer<Boolean> result) {
        EconomySubscriber
                .<Boolean>asFuture(s -> provider.hasPlayerAccount(player.getUniqueId(), s))
                .thenCompose(val -> {
                    if (val) {
                        return EconomySubscriber.<PlayerAccount>asFuture(s -> provider.retrievePlayerAccount(player.getUniqueId(), s));
                    } else {
                        return EconomySubscriber.<PlayerAccount>asFuture(s -> provider.createPlayerAccount(player.getUniqueId(), s));
                    }
                }).whenComplete((account, ex) -> {
                    account.withdrawBalance(
                            amount,
                            transactionInitiator,
                            currency,
                            new EconomySubscriber<BigDecimal>() {
                                @Override
                                public void succeed(@NotNull BigDecimal bigDecimal) {
                                    result.accept(true);
                                }

                                @Override
                                public void fail(@NotNull EconomyException exception) {
                                    result.accept(false);
                                    exception.printStackTrace();
                                }
                            });
                });
    }

    @Override
    public void initProvider() {
        Optional<Service<EconomyProvider>> service = ServiceRegistry.INSTANCE.serviceFor(EconomyProvider.class);

        if(!service.isPresent()) {
            Log.error("Unable to find a supported economy plugin for Treasury!");
            return;
        }

        provider = service.get().get();
        transactionInitiator = EconomyTransactionInitiator.createInitiator(EconomyTransactionInitiator.Type.PLUGIN, "HeadDB");

        String rawCurrency = HeadDB.getInstance().getConfig().getString("economy.currency");
        if (rawCurrency == null || rawCurrency.isEmpty()) {
            currency = provider.getPrimaryCurrency();
        } else {
            currency = provider.getCurrencies().stream()
                    .filter(currency -> currency.getIdentifier().equalsIgnoreCase(rawCurrency))
                    .findFirst().get();
        }
    }

}
