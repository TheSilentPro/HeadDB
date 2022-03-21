package tsp.headdb.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A button
 */
public class Button {

    private static int counter = 0;
    private static final int ID = counter++;

    private final ItemStack itemStack;
    private Consumer<InventoryClickEvent> action;

    /**
     * @param itemStack The Item
     */
    @SuppressWarnings("unused")
    public Button(ItemStack itemStack) {
        this(itemStack, event -> {
        });
    }

    /**
     * @param itemStack The Item
     * @param action The action
     */
    public Button(ItemStack itemStack, Consumer<InventoryClickEvent> action) {
        this.itemStack = itemStack;
        this.action = action;
    }



    /**
     * @return The icon
     */
    @SuppressWarnings("WeakerAccess")
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * @param action The new action
     */
    @SuppressWarnings("unused")
    public void setAction(Consumer<InventoryClickEvent> action) {
        this.action = action;
    }

    /**
     * @param event The event that triggered it
     */
    @SuppressWarnings("WeakerAccess")
    public void onClick(InventoryClickEvent event) {
        action.accept(event);
    }

    // We do not want equals collisions. The default hashcode would not fulfil this contract.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        return o instanceof Button;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }
}
