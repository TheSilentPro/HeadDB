package com.github.thesilentpro.headdb.core.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.thesilentpro.headdb.api.model.Head;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

@SuppressWarnings("deprecation")
public class Compatibility {

    private static final Logger LOGGER = LoggerFactory.getLogger(Compatibility.class);
    public static final boolean IS_PAPER;

    static {
        boolean isPaper;
        try {
            Class.forName("com.destroystokyo.paper.profile.PlayerProfile");
            isPaper = true;
        } catch (ClassNotFoundException e) {
            isPaper = false;
        }
        IS_PAPER = isPaper;
    }

    public static Executor getMainThreadExecutor(JavaPlugin plugin) {
        if (plugin == null) {
            throw new RuntimeException("Plugin instance is null!");
        }
        if (IS_PAPER) {
            return plugin.getServer().getScheduler().getMainThreadExecutor(plugin);
        } else {
            return r -> plugin.getServer().getScheduler().runTask(plugin, r);
        }
    }

    public static String getPluginVersion(JavaPlugin plugin) {
        if (plugin == null) {
            throw new RuntimeException("Plugin instance is null!");
        }
        if (IS_PAPER) {
            return plugin.getPluginMeta().getVersion();
        } else {
            return plugin.getDescription().getVersion();
        }
    }

    public static void sendMessage(CommandSender sender, Component component) {
        if (sender == null) {
            return;
        }
        if (component == null) {
            return; // Silently fail on null components to avoid exceptions
        }
        if (IS_PAPER) {
            sender.sendMessage(component);
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', LegacyComponentSerializer.legacyAmpersand().serialize(component)));
        }
    }

    public static ItemStack asItem(Head head) {
        ItemStack item;
        if (IS_PAPER) {
            item = ItemStack.of(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);
            try {
                PlayerTextures textures = profile.getTextures();
                textures.setSkin(URI.create("http://textures.minecraft.net/texture/" + head.getTexture()).toURL());
                profile.setTextures(textures);
            } catch (MalformedURLException ex) {
                LOGGER.error("Failed to set texture for {} (ID:{} | Texture: {})", head.getName(), head.getId(), head.getTexture(), ex);
                return item;
            }

            meta.itemName(Component.text(head.getName()));
            meta.lore(List.of(
                    Component.text("ID: ").color(NamedTextColor.GRAY).append(Component.text(head.getId()).color(NamedTextColor.RED)).decoration(TextDecoration.ITALIC, false),
                    Component.text("Category: ").color(NamedTextColor.GRAY).append(Component.text(head.getCategory()).color(NamedTextColor.GOLD)).decoration(TextDecoration.ITALIC, false),
                    Component.text("Tags: ").color(NamedTextColor.GRAY).append(Component.text(String.join(", ", head.getTags())).color(NamedTextColor.GOLD)).decoration(TextDecoration.ITALIC, false)
            ));
            meta.setPlayerProfile(profile);
            item.setItemMeta(meta);
        } else {
            item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            org.bukkit.profile.PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), null);
            try {
                PlayerTextures textures = profile.getTextures();
                textures.setSkin(URI.create("http://textures.minecraft.net/texture/" + head.getTexture()).toURL());
                profile.setTextures(textures);
            } catch (MalformedURLException ex) {
                LOGGER.error("Failed to set texture for {} (ID:{} | Texture: {})", head.getName(), head.getId(), head.getTexture(), ex);
                return item;
            }

            meta.setItemName(head.getName());
            meta.setOwnerProfile(profile);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack asItem(@Nullable OfflinePlayer player) {
        if (player == null) {
            return null;
        }

        ItemStack item;
        if (IS_PAPER) {
            item = ItemStack.of(Material.PLAYER_HEAD);
        } else {
            item = new ItemStack(Material.PLAYER_HEAD);
        }
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(player);

        if (player.getName() != null) { // Should never fail
            if (IS_PAPER) {
                meta.itemName(Component.text(player.getName()));
            } else {
                meta.setItemName(player.getName());
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    @Nullable
    public static UUID getIdFromItem(ItemStack item) {
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (IS_PAPER) {
            PlayerProfile profile = meta.getPlayerProfile();
            if (profile == null) {
                return null;
            }
            return profile.getId();
        } else {
            org.bukkit.profile.PlayerProfile profile = meta.getOwnerProfile();
            if (profile == null) {
                return null;
            }
            return profile.getUniqueId();
        }
    }

    public static Component getNameFromItem(ItemStack item) {
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (IS_PAPER) {
            return meta.itemName();
        } else {
            return Component.text(meta.getItemName());
        }
    }

    public static ItemStack setItemDetails(ItemStack item, Component name, @NotNull Component @Nullable ... lore) {
        ItemMeta meta = item.getItemMeta();
        if (IS_PAPER) {
            meta.itemName(name);
            if (lore != null) {
                meta.lore(Arrays.asList(lore));
            }
        } else {
            meta.setItemName(ChatColor.translateAlternateColorCodes('&', LegacyComponentSerializer.legacyAmpersand().serialize(name)));
            if (lore != null) {
                meta.setLore(Arrays.stream(lore).map(component -> ChatColor.translateAlternateColorCodes('&', LegacyComponentSerializer.legacyAmpersand().serialize(component))).toList());
            }
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack setItemDetails(ItemStack item, Component name) {
        return setItemDetails(item, name, (Component[]) null);
    }

    public static ItemStack newItem(Material material) {
        if (material == null) {
            return null;
        }
        return IS_PAPER ? ItemStack.of(material) : new ItemStack(material);
    }

    public static ItemStack newItem(Material material, Component name, Component... lore) {
        ItemStack item;
        if (IS_PAPER) {
            item = ItemStack.of(material);
        } else {
            item = new ItemStack(material);
        }
        return setItemDetails(item, name, lore);
    }
    
}
