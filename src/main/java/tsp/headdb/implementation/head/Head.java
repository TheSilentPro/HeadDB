package tsp.headdb.implementation.head;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tsp.headdb.HeadDB;
import tsp.smartplugin.builder.item.ItemBuilder;
import tsp.smartplugin.localization.TranslatableLocalization;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.UUID;

@ParametersAreNonnullByDefault
public record Head(UUID uniqueId, String name, String value, String tags, String updated) {

    private static ItemStack item;

    public ItemStack getItem(UUID receiver) {
        if (item == null) {
            TranslatableLocalization localization = HeadDB.getInstance().getLocalization();
            item = new ItemBuilder(Material.PLAYER_HEAD)
                    .name(localization.getMessage(receiver, "menu.head.name").orElse("&e" + name.toUpperCase(Locale.ROOT)).replace("%name%", name))
                    .setLore("&7Tags: &e" + tags)
                    .build();

            ItemMeta meta = item.getItemMeta();
            GameProfile profile = new GameProfile(uniqueId, name);
            profile.getProperties().put("textures", new Property("textures", value));
            Field profileField;
            try {
                //noinspection ConstantConditions
                profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, profile);
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
                //Log.error("Could not set skull owner for " + uuid.toString() + " | Stack Trace:");
                ex.printStackTrace();
            }

            item.setItemMeta(meta);
        }
        return item;
    }

}