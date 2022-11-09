package tsp.headdb.implementation.head;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tsp.headdb.HeadDB;
import tsp.headdb.implementation.category.Category;
import tsp.smartplugin.builder.item.ItemBuilder;
import tsp.smartplugin.localization.TranslatableLocalization;
import tsp.smartplugin.utils.Validate;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.util.Locale;
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
            TranslatableLocalization localization = HeadDB.getInstance().getLocalization();
            item = new ItemBuilder(Material.PLAYER_HEAD)
                    .name(localization.getMessage(receiver, "menu.head.name").orElse("&e" + name.toUpperCase(Locale.ROOT)).replace("%name%", name))
                    .setLore("&cID: " + id, "&7Tags: &e" + tags)
                    .build();

            ItemMeta meta = item.getItemMeta();
            GameProfile profile = new GameProfile(uniqueId, name);
            profile.getProperties().put("textures", new Property("textures", texture));
            try {
                //noinspection ConstantConditions
                Field profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, profile);
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
                //Log.error("Could not set skull owner for " + uuid.toString() + " | Stack Trace:");
                ex.printStackTrace();
            }

            item.setItemMeta(meta);
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