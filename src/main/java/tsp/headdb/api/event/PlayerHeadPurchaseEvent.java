package tsp.headdb.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import tsp.headdb.implementation.Head;

/**
 * This event is called when a player purchases a {@link Head}
 *
 * @author TheSilentPro
 * @see tsp.headdb.inventory.InventoryUtils#purchaseHead(Player, Head, int, String, String)
 */
public class PlayerHeadPurchaseEvent extends Event implements Cancellable {

    private final HandlerList handlerList = new HandlerList();
    private boolean cancelled;
    private Player player;
    private Head head;
    private double cost;

    public PlayerHeadPurchaseEvent(Player player, Head head, double cost) {
        super(true);
        this.player = player;
        this.head = head;
        this.cost = cost;
    }

    public Player getPlayer() {
        return player;
    }

    public Head getHead() {
        return head;
    }

    public double getCost() {
        return cost;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
