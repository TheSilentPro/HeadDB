package tsp.headdb.api;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import tsp.headdb.database.Category;
import tsp.headdb.util.Log;
import tsp.headdb.util.Utils;
import tsp.headdb.util.XMaterial;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Head {

    private String name;
    private UUID uuid;
    private String value;
    private Category category;
    private int id;
    private List<String> tags;

    public Head() {}

    public Head(int id) {
        this.id = id;
    }

    public ItemStack getItemStack() {
        Validate.notNull(name, "name must not be null!");
        Validate.notNull(uuid, "uuid must not be null!");
        Validate.notNull(value, "value must not be null!");

        ItemStack item = new ItemStack(XMaterial.PLAYER_HEAD.parseItem());
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName(Utils.colorize(category != null ? category.getColor() + name : "&8" + name));
        // set skull owner
        GameProfile profile = new GameProfile(uuid, name);
        profile.getProperties().put("textures", new Property("textures", value));
        Field profileField;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            Log.error("Could not set skull owner for " + uuid.toString() + " | Stack Trace:");
            e1.printStackTrace();
        }
        meta.setLore(Arrays.asList(
                Utils.colorize("&cID: " + id),
                Utils.colorize("&e" + buildTagLore((String[]) tags.toArray())),
                "",
                Utils.colorize("&8Right-Click to add/remove from favorites.")
        ));
        item.setItemMeta(meta);

        return item;
    }

    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getValue() {
        return value;
    }

    public Category getCategory() {
        return category;
    }

    public int getId() {
        return id;
    }

    public List<String> getTags() {
        return tags;
    }

    public Head withName(String name) {
        this.name = name;
        return this;
    }

    public Head withUUID(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public Head withValue(String value) {
        this.value = value;
        return this;
    }

    public Head withCategory(Category category) {
        this.category = category;
        return this;
    }

    public Head withId(int id) {
        this.id = id;
        return this;
    }
    
    public Head withTags(String tags) {
        this.tags = Arrays.asList(tags.split(","));
        return this;
    }
    
    private String buildTagLore(String... tags) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tags.length; i++) {
            builder.append(tags[i]);
            if (i != tags.length - 1) {
                builder.append(",");
            }
        }
        
        return builder.toString();
    }

}
