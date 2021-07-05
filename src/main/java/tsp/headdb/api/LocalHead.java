package tsp.headdb.api;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import tsp.headdb.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocalHead {

    private UUID uuid;
    private String name;

    public ItemStack getItemStack() {
        Validate.notNull(uuid, "uuid must not be null!");

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        meta.setDisplayName(Utils.colorize("&e" + name));
        List<String> lore = new ArrayList<>();
        lore.add(Utils.colorize("&7UUID: " + uuid.toString()));
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public LocalHead(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public LocalHead withUUID(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public LocalHead withName(String name) {
        this.name = name;
        return this;
    }

}
