/**
 * @author TheSilentPro (Silent)
 */
package tsp.headdb.api.model;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.core.util.Utils;

import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Optional;

public enum Category {

    ALPHABET("alphabet", "Alphabet", 20),
    ANIMALS("animals", "Animals", 21),
    BLOCKS("blocks", "Blocks", 22),
    DECORATION("decoration", "Decoration", 23),
    FOOD_DRINKS("food & drinks", "Food & Drinks", 24),
    HUMANS("humans", "Humans", 29),
    HUMANOID("humanoid", "Humanoid", 30),
    MISCELLANEOUS("miscellaneous", "Miscellaneous", 31),
    MONSTERS("monsters", "Monsters", 32),
    PLANTS("plants", "Plants", 33);

    private final String name;
    private final String displayName;
    private final int defaultSlot;
    private ItemStack item;

    public static final Category[] VALUES = values();

    Category(String name, String displayName, int defaultSlot) {
        this.name = name;
        this.displayName = displayName;
        this.defaultSlot = defaultSlot;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDefaultSlot() {
        return defaultSlot;
    }

    public static Optional<Category> getByName(String cname) {
        for (Category value : VALUES) {
            if (value.name.equalsIgnoreCase(cname) || value.name().equalsIgnoreCase(cname)) {
                return Optional.of(value);
            }
        }

        return Optional.empty();
    }

    @NotNull
    public ItemStack getDisplayItem() {
        if (item == null) {
            List<Head> headsList = HeadAPI.getAllHeads()
                    .stream()
                    .filter(head -> head.getCategory().orElse("N/A").equalsIgnoreCase(getName()))
                    .toList();

            headsList.stream()
                    .findFirst()
                    .ifPresentOrElse(head -> {
                                ItemStack retrieved = new ItemStack(head.getItem());
                                ItemMeta meta = retrieved.getItemMeta();
                                if (meta != null) {
                                    meta.setDisplayName(ChatColor.BOLD + "" + ChatColor.GOLD + getDisplayName().toUpperCase());
                                    meta.setLore(List.of(ChatColor.GRAY + "Total Heads » " + ChatColor.GOLD + headsList.size()));
                                    retrieved.setItemMeta(meta);
                                } else {
                                    retrieved = new ItemStack(Material.PLAYER_HEAD);
                                }
                                item = retrieved;
                            },
                            () -> {
                                ItemStack copy = new ItemStack(Material.PLAYER_HEAD);
                                ItemMeta meta = copy.getItemMeta();
                                //noinspection DataFlowIssue
                                meta.setDisplayName(Utils.colorize(getName().toUpperCase()));
                                copy.setItemMeta(meta);
                                item = copy;
                            });

        }

        return item.clone();
    }

}