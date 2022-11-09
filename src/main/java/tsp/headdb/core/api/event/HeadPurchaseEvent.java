package tsp.headdb.core.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import tsp.headdb.implementation.head.Head;

import java.math.BigDecimal;

/**
 * Called <strong>BEFORE</strong> a head is purchased but <strong>AFTER</strong> the transaction is complete.
 * This gives you the chance to cancel and refund the money.
 * <strong>This event is fired asynchronously!</strong>
 *
 * @author TheSilentPro (Silent)
 * @see tsp.headdb.core.util.Utils#processPayment(Player, Head)
 * @see Event#isAsynchronous()
 */
public class HeadPurchaseEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private Player player;
    private Head head;
    private BigDecimal cost;
    private final boolean success;
    private boolean cancelled;

    public HeadPurchaseEvent(Player player, Head head, BigDecimal cost, boolean success) {
        super(true);
        this.player = player;
        this.head = head;
        this.cost = cost;
        this.success = success;
    }

    public boolean isSuccessful() {
        return success;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
}