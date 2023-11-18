package tsp.headdb.implementation.head;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.UUID;

public record LocalHead(UUID uniqueId, String name) {

    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(uniqueId));
            meta.setDisplayName(ChatColor.GOLD + name);
            //noinspection UnnecessaryToStringCall
            meta.setLore(Collections.singletonList(ChatColor.GRAY + "UUID: " + uniqueId.toString()));
            item.setItemMeta(meta);
        }
        return item;
    }

}
