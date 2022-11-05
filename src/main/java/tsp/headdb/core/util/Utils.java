package tsp.headdb.core.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tsp.headdb.HeadDB;
import tsp.headdb.implementation.category.Category;
import tsp.headdb.implementation.head.Head;
import tsp.smartplugin.inventory.Button;
import tsp.smartplugin.inventory.paged.PagedPane;
import tsp.smartplugin.utils.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

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
        provided = provided.toLowerCase(Locale.ROOT);
        query = query.toLowerCase(Locale.ROOT);
        return provided.equals(query)
                || provided.startsWith(query)
                || provided.contains(query)
                || provided.endsWith(query);
    }

    @ParametersAreNonnullByDefault
    public static void addHeads(Player player, @Nullable Category category, PagedPane pane, Collection<Head> heads) {
        for (Head head : heads) {
            ItemStack item = head.getItem(player.getUniqueId());
            pane.addButton(new Button(head.getItem(player.getUniqueId()), e -> {
                if (category != null && instance.getConfig().getBoolean("requireCategoryPermission") && !player.hasPermission("headdb.category." + category.getName())) {
                    instance.getLocalization().sendMessage(player.getUniqueId(), "noPermission");
                    e.setCancelled(true);
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

                e.setCancelled(true);
           }));
        }
    }

}
