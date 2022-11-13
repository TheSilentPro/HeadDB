package tsp.headdb.implementation.category;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tsp.headdb.HeadDB;
import tsp.headdb.core.api.HeadAPI;
import tsp.headdb.core.util.Utils;
import tsp.smartplugin.builder.item.ItemBuilder;
import tsp.smartplugin.utils.StringUtils;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public enum Category {

    ALPHABET("alphabet", 20),
    ANIMALS("animals", 21),
    BLOCKS("blocks", 22),
    DECORATION("decoration", 23),
    FOOD_DRINKS("food-drinks", 24),
    HUMANS("humans", 29),
    HUMANOID("humanoid", 30),
    MISCELLANEOUS("miscellaneous", 31),
    MONSTERS("monsters", 32),
    PLANTS("plants", 33);

    private final String name;
    private final int defaultSlot;
    private ItemStack item;

    public static final Category[] VALUES = values();

    Category(String name, int slot) {
        this.name = name;
        this.defaultSlot = slot;
    }

    public String getName() {
        return name;
    }

    public int getDefaultSlot() {
        return defaultSlot;
    }

    public static Optional<Category> getByName(String cname) {
        for (Category value : VALUES) {
            if (value.name.equalsIgnoreCase(cname) || value.getName().equalsIgnoreCase(cname)) {
                return Optional.of(value);
            }
        }

        return Optional.empty();
    }

    @Nonnull
    public ItemStack getItem(UUID receiver) {
        if (item == null) {
            HeadAPI.getHeads(this).stream().findFirst()
                    .ifPresentOrElse(head -> {
                                ItemStack retrieved = new ItemStack(head.getItem(receiver));
                                ItemMeta meta = retrieved.getItemMeta();
                                if (meta != null && meta.getLore() != null) {
                                    meta.setDisplayName(Utils.translateTitle(HeadDB.getInstance().getLocalization().getMessage(receiver, "menu.main.category.name").orElse("&e" + getName()), HeadAPI.getHeads(this).size(), getName().toUpperCase(Locale.ROOT)));
                                    meta.setLore(HeadDB.getInstance().getConfig().getStringList("menu.main.category.lore").stream()
                                            .map(StringUtils::colorize)
                                            .collect(Collectors.toList()));
                                    retrieved.setItemMeta(meta);
                                    item = retrieved;
                                } else {
                                    item = new ItemStack(Material.PLAYER_HEAD);
                                    HeadDB.getInstance().getLog().debug("Failed to get null-meta category item for: " + name());
                                }
                            },
                    () -> item = new ItemBuilder(Material.PLAYER_HEAD).name(getName().toUpperCase(Locale.ROOT)).build());
        }

        return item.clone(); // Return clone that changes are not reflected
    }

}
