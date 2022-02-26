package tsp.headdb.implementation;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import tsp.headdb.api.HeadAPI;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a category for heads
 *
 * @author TheSilentPro
 */
public enum Category {

    ALPHABET("alphabet", ChatColor.YELLOW, 20),
    ANIMALS("animals", ChatColor.DARK_AQUA, 21),
    BLOCKS("blocks", ChatColor.DARK_GRAY, 22),
    DECORATION("decoration", ChatColor.LIGHT_PURPLE, 23),
    FOOD_DRINKS("food-drinks", ChatColor.GOLD, 24),
    HUMANS("humans", ChatColor.DARK_BLUE, 29),
    HUMANOID("humanoid", ChatColor.AQUA, 30),
    MISCELLANEOUS("miscellaneous", ChatColor.DARK_GREEN, 31),
    MONSTERS("monsters", ChatColor.RED, 32),
    PLANTS("plants", ChatColor.GREEN, 33);

    private final String name;
    private final ChatColor color;
    private final int location;
    private final Map<Category, Head> item = new HashMap<>();
    public static final Category[] cache = values();

    Category(String name, ChatColor color, int location) {
        this.name = name;
        this.color = color;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public int getLocation() {
        return location;
    }

    /**
     * Retrieve the first valid head from a category
     *
     * @return First valid head
     */
    public ItemStack getItem() {
        return HeadAPI.getHeads(this).stream()
                .filter(head -> head != null)
                .findFirst().get().getMenuItem();
    }

    /**
     * Retrieve a {@link Category} by name
     *
     * @param name The name
     * @return The category if it exists. Else it returns null
     */
    @Nullable
    public static Category getByName(String name) {
        for (Category category : cache) {
            if (category.getName().equalsIgnoreCase(name)) {
                return category;
            }
        }

        return null;
    }

}
