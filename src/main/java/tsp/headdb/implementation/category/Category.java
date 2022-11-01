package tsp.headdb.implementation.category;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import tsp.headdb.HeadDB;
import tsp.headdb.core.api.HeadAPI;
import tsp.headdb.implementation.head.Head;
import tsp.smartplugin.builder.item.ItemBuilder;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public enum Category {

    //alphabet, animals, blocks, decoration, food-drinks, humans, humanoid, miscellaneous, monsters, plants
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
    private final String url;

    public static final Category[] VALUES = values();

    Category(String name) {
        this.name = name;
        this.url = String.format("https://minecraft-heads.com/scripts/api.php?cat=%s&tags=true", name);
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    @Nonnull
    public ItemStack getItem(UUID receiver) {
        List<Head> heads = HeadAPI.getHeads(this);
        return heads.size() > 1
                ? new ItemBuilder(heads.get(0).getItem(receiver)).name(HeadDB.getInstance().getLocalization().getMessage(receiver, "menu.main.category.name").orElse("&e" + getName())).build()
                : new ItemBuilder(Material.BARRIER).name(getName().toUpperCase(Locale.ROOT)).build();
    }

}
