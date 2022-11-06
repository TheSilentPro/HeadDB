package tsp.headdb.implementation.category;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import tsp.headdb.HeadDB;
import tsp.headdb.core.api.HeadAPI;
import tsp.headdb.core.util.Utils;
import tsp.smartplugin.builder.item.ItemBuilder;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public enum Category {

    ALPHABET("alphabet"),
    ANIMALS("animals"),
    BLOCKS("blocks"),
    DECORATION("decoration"),
    FOOD_DRINKS("food-drinks"),
    HUMANS("humans"),
    HUMANOID("humanoid"),
    MISCELLANEOUS("miscellaneous"),
    MONSTERS("monsters"),
    PLANTS("plants");

    private final String name;
    private ItemStack item;

    public static final Category[] VALUES = values();

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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
                    .ifPresentOrElse(head -> item = new ItemBuilder(head.getItem(receiver))
                                    .name(Utils.translateTitle(HeadDB.getInstance().getLocalization().getMessage(receiver, "menu.category.name").orElse("&e" + getName()), HeadAPI.getHeads(this).size(), getName().toUpperCase(Locale.ROOT)))
                                    .setLore((String[]) null)
                                    .build(),
                    () -> item = new ItemBuilder(Material.PLAYER_HEAD).name(getName().toUpperCase(Locale.ROOT)).build());
        }

        return item;
    }

}
