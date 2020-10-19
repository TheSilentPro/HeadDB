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
import java.util.Collections;
import java.util.UUID;

public class Head {

    private String name;
    private UUID uuid;
    private String value;
    private Category category;
    private int id;

    public ItemStack getItemStack() {
        Validate.notNull(name, "name must not be null!");
        Validate.notNull(uuid, "uuid must not be null!");
        Validate.notNull(value, "value must not be null!");
        Validate.notNull(category, "category must not be null!");

        ItemStack item = XMaterial.PLAYER_HEAD.parseItem();
        if (item != null) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setDisplayName(Utils.colorize(category.getColor() + name));
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
            meta.setLore(Collections.singletonList(Utils.colorize("&cID: " + id)));
            item.setItemMeta(meta);
        }

        return item;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static class Builder {

        private String name;
        private UUID uuid;
        private String value;
        private Category category;
        private int id;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withUUID(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder withValue(String value) {
            this.value = value;
            return this;
        }

        public Builder withCategory(Category category) {
            this.category = category;
            return this;
        }

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Head build() {
            Head head = new Head();
            head.setName(name);
            head.setUUID(uuid);
            head.setValue(value);
            head.setCategory(category);
            head.setId(id);
            return head;
        }

    }

}
