package tsp.headdb.core.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tsp.headdb.HeadDB;
import tsp.headdb.core.config.ConfigData;
import tsp.headdb.core.economy.EconomyProvider;
import tsp.headdb.api.model.Head;
import tsp.headdb.api.model.LocalHead;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * @author TheSilentPro (Silent)
 */
public class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);
    private static final Pattern HEAD_PATTERN = Pattern.compile("[^a-zA-Z0-9]");
    private static final Pattern SPACE_PATTERN = Pattern.compile(" ");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final Executor SYNC = r -> Bukkit.getScheduler().runTask(HeadDB.getInstance(), r);
    private static final ConfigData config = HeadDB.getInstance().getCfg();

    @SuppressWarnings("DataFlowIssue")
    public static ItemStack asItem(Head head) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + head.getName());

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "ID » " + ChatColor.GOLD + head.getId());
        head.getTags().ifPresent(tags -> lore.add(ChatColor.GRAY + "Tags » " + ChatColor.GOLD + String.join(", ", tags)));

        if (config.shouldIncludeMoreInfo()) {
            head.getCategory().ifPresent(category -> {
                if (!category.isEmpty()) {
                    lore.add(ChatColor.GRAY + "Category » " + ChatColor.GOLD + category);
                }
            });
            head.getContributors().ifPresent(contributors -> {
                if (contributors.length != 0) {
                    lore.add(ChatColor.GRAY + "Contributors » " + ChatColor.GOLD + String.join(", ", contributors));
                }
            });
            head.getCollections().ifPresent(collections -> {
                if (collections.length != 0) {
                    lore.add(ChatColor.GRAY + "Collections » " + ChatColor.GOLD + String.join(", ", collections));
                }
            });
            head.getPublishDate().ifPresent(date -> lore.add(ChatColor.GRAY + "Published » " + ChatColor.GOLD + date));
        }

        if (HeadDB.getInstance().getEconomyProvider() != null) {
            lore.add(" ");
            lore.add(ChatColor.GRAY + "Cost (x1) » " + ChatColor.GOLD + getHeadCost(head) + ChatColor.GRAY + " (Left-Click)");
            lore.add(ChatColor.GRAY + "Cost (x64) » " + ChatColor.GOLD + (getHeadCost(head) * 64) + ChatColor.GRAY + " (Shift-Left-Click)");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        PlayerProfile profile;
        try {
            profile = Bukkit.createPlayerProfile(null, head.getName());
        } catch (IllegalArgumentException ex) {
            // Head may contain special characters(@,!,<,>) that are not allowed in a PlayerProfile.
            // Additionally, spaces are also removed as the profile name should not be visible to players.
            String name = HEAD_PATTERN.matcher(head.getName().trim()).replaceAll("");
            if (name.length() > 16) { // Profile names can not be longer than 16 characters
                name = name.substring(0, 16);
            }
            profile = Bukkit.createPlayerProfile(null, name);
        }

        PlayerTextures textures = profile.getTextures();
        String url = new String(Base64.getDecoder().decode(head.getTexture().orElseThrow(() -> new IllegalArgumentException("Head texture must not be null!"))));
        try {
            textures.setSkin(URI.create(url.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(), url.length() - "\"}}}".length())).toURL());
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
        profile.setTextures(textures);

        if (meta instanceof SkullMeta skullMeta) {
            skullMeta.setOwnerProfile(profile);
            item.setItemMeta(skullMeta);
        }
        return item;
    }

    public static void purchaseHead(Player player, Head head, int amount) {
        EconomyProvider economyProvider = HeadDB.getInstance().getEconomyProvider();
        if (economyProvider == null) {
            ItemStack item = head.getItem().clone();
            item.setAmount(amount);
            if (!HeadDB.getInstance().getCfg().shouldIncludeLore()) {
                ItemMeta meta = item.getItemMeta();
                //noinspection DataFlowIssue
                meta.setLore(null);
                item.setItemMeta(meta);
            }
            player.getInventory().addItem(item);
            Sounds.SUCCESS.play(player);
            return;
        }

        double cost = getHeadCost(head);
        economyProvider.purchase(player, cost * amount).whenComplete((success, ex) -> {
            Bukkit.getScheduler().runTask(HeadDB.getInstance(), () -> {
                if (ex != null) {
                    HeadDB.getInstance().getLocalization().sendMessage(player, "error");
                    LOGGER.error("Purchasing head(s) failed!", ex);
                    return;
                }

                if (success) {
                    ItemStack item = head.getItem().clone();
                    item.setAmount(amount);
                    if (!HeadDB.getInstance().getCfg().shouldIncludeLore()) {
                        ItemMeta meta = item.getItemMeta();
                        //noinspection DataFlowIssue
                        meta.setLore(null);
                        item.setItemMeta(meta);
                    }
                    player.getInventory().addItem(item);
                    Sounds.SUCCESS.play(player);

                    HeadDB.getInstance().getLocalization().sendMessage(player, "completePayment", msg -> msg.replace("%amount%", String.valueOf(amount)).replace("%name%", head.getName()).replace("%cost%", String.valueOf(cost * amount)));

                    HeadDB.getInstance().getConfig().getStringList("commands.purchase").forEach(command -> {
                        if (command.isEmpty()) {
                            return;
                        }
                        if (HeadDB.getInstance().isPAPI()) {
                            command = PlaceholderAPI.setPlaceholders(player, command);
                        }

                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    });
                } else {
                    Sounds.FAIL.play(player);
                    HeadDB.getInstance().getLocalization().sendMessage(player, "invalidFunds", msg -> msg.replace("%amount%", String.valueOf(amount)).replace("%name%", head.getName()).replace("%cost%", String.valueOf(cost * amount)));
                }
            });
        });
    }

    public static double getHeadCost(Head head) {
        if (head instanceof LocalHead) { // Local heads have only one cost
            return config.getLocalCost();
        } else if (config.getCosts().containsKey(head)) { // Try get cost for specific head
            return config.getCosts().get(head);
        } else if (config.getCategoryCosts().containsKey(head.getCategory().orElse("?"))) { // Try get cost for specific category for the head
            return config.getCategoryCosts().get(head.getCategory().orElse("?"));
        } else { // Get the default cost for the head.
            return config.getDefaultCost();
        }
    }

    public static String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private static String userAgent = null;

    public static String getUserAgent() {
        if (userAgent == null) {
            // Example output: HeadDB/5.0.0 (Windows 10; 10.0; amd64) Eclipse Adoptium/21.0.4 Paper/1.21.1 (1.21.1-40-2fdb2e9)
            userAgent = "HeadDB/" + HeadDB.getInstance().getDescription().getVersion() +
                    " (" + System.getProperty("os.name") +
                    "; " + System.getProperty("os.version") +
                    "; " + System.getProperty("os.arch") +
                    ") " + System.getProperty("java.vendor") + "/" + System.getProperty("java.version") +
                    " " + Bukkit.getName() + "/" + Bukkit.getBukkitVersion().substring(0, Bukkit.getBukkitVersion().indexOf("-")) + " (" + Bukkit.getVersion().substring(0, Bukkit.getVersion().indexOf("(") - 1) + ")";
        }
        return userAgent;
    }

    public static boolean matches(String provided, String query) {
        provided = ChatColor.stripColor(provided);
        if (provided.equalsIgnoreCase(query)) {
            return true;
        }

        for (String arg : SPACE_PATTERN.split(provided)) {
            if (arg.equalsIgnoreCase(query)) {
                return true;
            }
        }

        return false;
    }

    public static ExecutorService from(int threads, String name) {
        return threads == 1 ? Executors.newSingleThreadExecutor(r -> new Thread(r, name)) : Executors.newFixedThreadPool(threads, r -> new Thread(r, name));
    }

}
