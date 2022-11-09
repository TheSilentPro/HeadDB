package tsp.headdb.core.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tsp.headdb.HeadDB;
import tsp.headdb.core.api.HeadAPI;
import tsp.headdb.core.api.event.HeadPurchaseEvent;
import tsp.headdb.core.economy.BasicEconomyProvider;
import tsp.headdb.core.hook.Hooks;
import tsp.headdb.implementation.category.Category;
import tsp.headdb.implementation.head.Head;
import tsp.smartplugin.inventory.Button;
import tsp.smartplugin.inventory.PagedPane;
import tsp.smartplugin.utils.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Utils {

    private static final HeadDB instance = HeadDB.getInstance();

    public static Optional<UUID> validateUniqueId(@Nonnull String raw) {
        try {
            return Optional.of(UUID.fromString(raw));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    @ParametersAreNonnullByDefault
    public static String translateTitle(String raw, int size, String category, @Nullable String query) {
        return StringUtils.colorize(raw)
                .replace("%size%", String.valueOf(size))
                .replace("%category%", category)
                .replace("%query%", (query != null ? query : "%query%"));
    }

    @ParametersAreNonnullByDefault
    public static String translateTitle(String raw, int size, String category) {
        return translateTitle(raw, size, category, null);
    }

    public static boolean matches(String provided, String query) {
        provided = ChatColor.stripColor(provided.toLowerCase(Locale.ROOT));
        query = query.toLowerCase(Locale.ROOT);
        return provided.equals(query)
                || provided.startsWith(query)
                || provided.contains(query);
                //|| provided.endsWith(query);
    }

    public static PagedPane createPaged(Player player, String title) {
        PagedPane main = new PagedPane(4, 6, title);
        HeadAPI.getHeadByTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY1MmUyYjkzNmNhODAyNmJkMjg2NTFkN2M5ZjI4MTlkMmU5MjM2OTc3MzRkMThkZmRiMTM1NTBmOGZkYWQ1ZiJ9fX0=").ifPresent(head -> main.setBackItem(head.getItem(player.getUniqueId())));
        HeadAPI.getHeadByTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Q5MWY1MTI2NmVkZGM2MjA3ZjEyYWU4ZDdhNDljNWRiMDQxNWFkYTA0ZGFiOTJiYjc2ODZhZmRiMTdmNGQ0ZSJ9fX0=").ifPresent(head -> main.setCurrentItem(head.getItem(player.getUniqueId())));
        HeadAPI.getHeadByTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmEzYjhmNjgxZGFhZDhiZjQzNmNhZThkYTNmZTgxMzFmNjJhMTYyYWI4MWFmNjM5YzNlMDY0NGFhNmFiYWMyZiJ9fX0=").ifPresent(head -> main.setNextItem(head.getItem(player.getUniqueId())));

        main.setControlCurrent(new Button(main.getCurrentItem(), e -> Bukkit.dispatchCommand(player, "hdb")));
        return main;
    }

    @ParametersAreNonnullByDefault
    public static void addHeads(Player player, @Nullable Category category, PagedPane pane, Collection<Head> heads) {
        for (Head head : heads) {
            ItemStack item = head.getItem(player.getUniqueId());
            pane.addButton(new Button(item, e -> {
                e.setCancelled(true);

                if (category != null && instance.getConfig().getBoolean("requireCategoryPermission") && !player.hasPermission("headdb.category." + category.getName())) {
                    instance.getLocalization().sendMessage(player.getUniqueId(), "noPermission");
                    return;
                }

                if (e.isLeftClick()) {
                    if (e.isShiftClick()) {
                        item.setAmount(64);
                    }

                    e.getWhoClicked().getInventory().addItem(item);
                } else if (e.isRightClick()) {
                    // todo: favorites
                }
           }));
        }
    }

    public static CompletableFuture<Boolean> processPayment(Player player, Head head) {
        BigDecimal cost = BigDecimal.valueOf(HeadDB.getInstance().getConfig().getDouble("economy.cost." + head.getCategory().getName()));

        Optional<BasicEconomyProvider> optional = HeadDB.getInstance().getEconomyProvider();
        if (optional.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        } else {
            return optional.get().purchase(player, cost).thenApply(success -> {
                HeadPurchaseEvent event = new HeadPurchaseEvent(player, head, cost, success);
                Bukkit.getPluginManager().callEvent(event);
                return !event.isCancelled() && success;
            });
        }
    }

    public static void purchase(Player player, Head head) {
        processPayment(player, head).whenComplete((success, ex) -> {
            if (ex != null) {
                HeadDB.getInstance().getLog().error("Failed to purchase head '" + head.getName() + "' for player: " + player.getName());
                ex.printStackTrace();
            } else {
                // Bukkit API, therefore task is ran sync. Needs testing not sure if this can be ran async.
                Bukkit.getScheduler().runTask(HeadDB.getInstance(), () -> {
                    player.getInventory().addItem(head.getItem(player.getUniqueId()));
                    HeadDB.getInstance().getConfig().getStringList("commands.purchase").forEach(command -> {
                        if (Hooks.PAPI.enabled()) {
                            command = PlaceholderAPI.setPlaceholders(player, command);
                        }

                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    });
                });
            }
        });
    }

    public static Optional<String> getTexture(ItemStack head) {
        ItemMeta meta = head.getItemMeta();
        if (meta == null) {
            return Optional.empty();
        }

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            GameProfile profile = (GameProfile) profileField.get(meta);
            if (profile == null) {
                return Optional.empty();
            }

            return profile.getProperties().get("textures").stream()
                    .filter(p -> p.getName().equals("textures"))
                    .findAny()
                    .map(Property::getValue);
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e ) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static int resolveInt(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException nfe) {
            return 1;
        }
    }

}
