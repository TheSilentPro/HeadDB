package tsp.headdb.api.model;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tsp.headdb.HeadDB;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a head from a player that has joined the server.
 *
 * @author TheSilentPro (Silent)
 */
public class LocalHead extends Head {

    private final UUID uuid;

    public LocalHead(int id, @NotNull UUID uniqueId, @NotNull String name, @Nullable String date) {
        super(-id, name, null, "Local Head", date, new String[]{"Local Heads"}, null, null);
        this.uuid = uniqueId;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    @Nullable
    @Override
    public Optional<String> getCategory() {
        return Optional.of("Local");
    }

    @NotNull
    @Override
    public ItemStack getItem() {
        if (!HeadDB.getInstance().getCfg().isLocalHeadsEnabled()) {
            return new ItemStack(Material.PLAYER_HEAD);
        }
        if (this.item == null) {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();

            if (meta != null) {
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(getUniqueId()));
                meta.setDisplayName(ChatColor.GOLD + getName());
                if (getPublishDate().isPresent()) {
                    meta.setLore(List.of(ChatColor.GRAY + "UUID » " + ChatColor.GOLD + getUniqueId().toString(), getPublishDate().isPresent() ? (ChatColor.GRAY + "First Joined » " + ChatColor.GOLD + getPublishDate().get()) : ""));
                } else {
                    meta.setLore(List.of(ChatColor.GRAY + "UUID » " + ChatColor.GOLD + getUniqueId().toString()));
                }
                item.setItemMeta(meta);
            }

            this.item = new ItemStack(item);
        }

        return item;
    }

}