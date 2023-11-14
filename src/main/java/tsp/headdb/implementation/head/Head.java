package tsp.headdb.implementation.head;

import org.bukkit.inventory.ItemStack;
import tsp.headdb.core.util.Utils;
import tsp.headdb.implementation.category.Category;
import tsp.nexuslib.util.Validate;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

public class Head {

    private final int id;
    private final UUID uniqueId;
    private final String name;
    private final String texture;
    private final String tags;
    private final String updated;
    private final Category category;
    private ItemStack item;

    @ParametersAreNonnullByDefault
    public Head(int id, UUID uniqueId, String name, String texture, String tags, String updated, Category category) {
        Validate.notNull(uniqueId, "Unique id can not be null!");
        Validate.notNull(name, "Name can not be null!");
        Validate.notNull(texture, "Texture can not be null!");
        Validate.notNull(tags, "Tags can not be null!");
        Validate.notNull(updated, "Updated can not be null!");
        Validate.notNull(category, "Category can not be null!");

        this.id = id;
        this.uniqueId = uniqueId;
        this.name = name;
        this.texture = texture;
        this.tags = tags;
        this.updated = updated;
        this.category = category;
    }

    public ItemStack getItem(UUID receiver) {
        if (item == null) {
            item = Utils.asItem(receiver, this);
        }

        return item.clone(); // Return clone that changes are not reflected
    }

    public int getId() {
        return id;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public String getTexture() {
        return texture;
    }

    public String getTags() {
        return tags;
    }

    public String getUpdated() {
        return updated;
    }

    public Category getCategory() {
        return category;
    }

}