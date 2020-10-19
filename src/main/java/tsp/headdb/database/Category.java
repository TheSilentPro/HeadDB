package tsp.headdb.database;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import tsp.headdb.api.Head;
import tsp.headdb.api.HeadAPI;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Category {

    ALPHABET("alphabet", ChatColor.YELLOW),
    ANIMALS("animals", ChatColor.DARK_AQUA),
    BLOCKS("blocks", ChatColor.DARK_GRAY),
    DECORATION("decoration", ChatColor.LIGHT_PURPLE),
    FOOD_DRINKS("food-drinks", ChatColor.GOLD),
    HUMANS("humans", ChatColor.DARK_BLUE),
    HUMANOID("humanoid", ChatColor.AQUA),
    MISCELLANEOUS("miscellaneous", ChatColor.DARK_GREEN),
    MONSTERS("monsters", ChatColor.RED),
    PLANTS("plants", ChatColor.GREEN);

    private final String name;
    private final ChatColor color;
    private final Map<Category, Head> item = new HashMap<>();

    Category(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public ItemStack getItem() {
        if (item.containsKey(this)) {
            return item.get(this).getItemStack();
        }

        item.put(this, HeadAPI.getHeads(this).get(0));
        return getItem();
    }

    public static Category getByName(String name) {
        for (Category category : Category.values()) {
            if (category.getName().equals(name)) {
                return category;
            }
        }

        return null;
    }

    public static List<Category> getCategories() {
        return Arrays.asList(Category.values());
    }

}
