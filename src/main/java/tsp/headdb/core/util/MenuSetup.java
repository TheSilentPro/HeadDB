package tsp.headdb.core.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tsp.headdb.HeadDB;
import tsp.headdb.api.model.Category;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.api.model.Head;
import tsp.headdb.api.model.LocalHead;
import tsp.headdb.core.player.PlayerData;
import tsp.invlib.gui.GUI;
import tsp.invlib.gui.SimpleGUI;
import tsp.invlib.gui.button.SimpleButton;
import tsp.invlib.gui.page.PageBuilder;

import java.util.*;

/**
 * @author TheSilentPro (Silent)
 */
@SuppressWarnings("DataFlowIssue")
public final class MenuSetup {

    public static final GUI mainGui = new SimpleGUI();
    public static final GUI allGui = new SimpleGUI();
    public static final Map<Category, GUI> categoryGuis = new HashMap<>();

    private static final Localization localization = HeadDB.getInstance().getLocalization();
    private static final ItemStack FILLER;
    private static final boolean categoryPermission = HeadDB.getInstance().getCfg().shouldRequireCategoryPermission();

    static {
        FILLER = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = FILLER.getItemMeta();
        //noinspection DataFlowIssue
        fillerMeta.setDisplayName(" ");
        FILLER.setItemMeta(fillerMeta);

        // Prebuild main page
        PageBuilder builder = new PageBuilder(mainGui)
                .fill(new SimpleButton(FILLER, e -> e.setCancelled(true)))
                .name(ChatColor.RED + "HeadDB" + ChatColor.DARK_GRAY + " (" + HeadAPI.getTotalHeads() + ")")
                .onClick(event -> {
                    // Compare material type instead of ItemStack because performance.
                    if (event.getCurrentItem().getType() != FILLER.getType()) {
                        Sounds.PAGE_OPEN.play((Player) event.getWhoClicked());
                    }
                })
                .preventClick();

        // Settings button


        // Set all heads button in main page
        ItemStack allItem = new ItemStack(Material.BOOK);
        ItemMeta meta = allItem.getItemMeta();
        meta.setItemName(ChatColor.GOLD + "All Heads");
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "Total heads » " + ChatColor.GOLD + HeadAPI.getTotalHeads()));
        allItem.setItemMeta(meta);
        builder.button(40, new SimpleButton(allItem, e -> {
            Player player = (Player) e.getWhoClicked();
            if (!player.hasPermission("headdb.category.all") && !player.hasPermission("headdb.category.*")) {
                localization.sendMessage(player, "noPermission");
                return;
            }

            if (e.isLeftClick()) {
                allGui.open(player);
            } else if (e.isRightClick()) {
                // todo: input specific page
            }
        }));

        // Set category buttons in main page
        for (Category category : Category.VALUES) {
            builder.button(category.getDefaultSlot(), new SimpleButton(category.getDisplayItem(), e -> {
                e.setCancelled(true);
                if (e.isLeftClick()) {
                    categoryGuis.get(category).open((Player) e.getWhoClicked());
                } else if (e.isRightClick()) {
                    // todo: input specific page
                }
            }));
        }

        // Set Favorites button in main page
        ItemStack favoritesHeadsItem = HeadAPI.getHeadByTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzZmZGQ0YjEzZDU0ZjZjOTFkZDVmYTc2NWVjOTNkZDk0NThiMTlmOGFhMzRlZWI1YzgwZjQ1NWIxMTlmMjc4In19fQ==")
                .join()
                .map(Head::getItem) // Get the ItemStack if the Head is present
                .orElse(new ItemStack(Material.BOOK)); // Default to BOOK if not present
        ItemMeta favoriteHeadsItemMeta = favoritesHeadsItem.getItemMeta();
        favoriteHeadsItemMeta.setDisplayName(ChatColor.GOLD + "Favorites");
        favoriteHeadsItemMeta.setLore(null);
        favoritesHeadsItem.setItemMeta(favoriteHeadsItemMeta);
        builder.button(39, new SimpleButton(favoritesHeadsItem, e -> {
            Player player = (Player) e.getWhoClicked();
            if (!player.hasPermission("headdb.favorites")) {
                localization.sendMessage(player, "noAccessFavorites");
                return;
            }

            favorites(player, 0);
        }));


        // Set local heads button in main page
        ItemStack localHeadsItem = new ItemStack(Material.COMPASS);
        ItemMeta localHeadsItemMeta = localHeadsItem.getItemMeta();
        //noinspection DataFlowIssue
        localHeadsItemMeta.setDisplayName(ChatColor.GOLD + "Local Heads");
        localHeadsItem.setItemMeta(localHeadsItemMeta);
        builder.button(41, new SimpleButton(localHeadsItem, e -> {
            HeadAPI.getLocalHeads(e.getWhoClicked().hasPermission("headdb.admin")).thenAcceptAsync(heads -> {
                GUI localGui = new SimpleGUI();
                Player mainPlayer = (Player) e.getWhoClicked();
                List<LocalHead> localHeadsList = new ArrayList<>(heads);

                for (int i = 0; i < localHeadsList.size(); i += 45) {
                    int end = Math.min(i + 45, localHeadsList.size());
                    List<LocalHead> section = localHeadsList.subList(i, end); // Get the sublist for the current page

                    PaginationBuilder localPageBuilder = new PaginationBuilder(localGui)
                            .parentGui(mainGui)
                            .name(ChatColor.RED + "Local Heads" + ChatColor.DARK_GRAY + " (" + heads.size() + ")");

                    // Iterate over the heads in the current section and add them to the inventory
                    for (int j = 0; j < section.size(); j++) {
                        LocalHead localHead = section.get(j);
                        localPageBuilder.button(j, new SimpleButton(localHead.getItem(), ice -> {
                            ice.setCancelled(true);
                            Player player = (Player) ice.getWhoClicked();
                            if (categoryPermission && !player.hasPermission("headdb.category.local.*") && !player.hasPermission("headdb.category.local." + localHead.getUniqueId())) {
                                localization.sendMessage(player, "noPermission");
                                return;
                            }

                            handleClick(player, localHead, ice);
                        }));
                    }

                    // Build the page and add it to the local GUI
                    localGui.addPage(localPageBuilder.build());
                    localGui.open(mainPlayer);
                }
            }, Utils.SYNC);
        }));

        mainGui.addPage(builder.build());

        prebuildCategoryGuis();
    }

    private static void favorites(Player player, int page) {
        HeadAPI.getFavoriteHeads(player.getUniqueId()).thenAcceptAsync(favorites -> {
            if (!favorites.isEmpty()) {
                // Build favorites GUI
                GUI favoritesGui = new SimpleGUI();
                List<Head> favoriteList = new ArrayList<>(favorites); // Copy to list for consistent indexing

                for (int i = 0; i < favoriteList.size(); i += 45) {
                    int end = Math.min(i + 45, favoriteList.size());
                    List<Head> section = favoriteList.subList(i, end);

                    PaginationBuilder favoritesPageBuilder = new PaginationBuilder(favoritesGui)
                            .parentGui(mainGui)
                            .name(ChatColor.GOLD + "Favorites " + ChatColor.DARK_GRAY + "(" + favoriteList.size() + ")");

                    for (int j = 0; j < section.size(); j++) {
                        Head head = section.get(j);
                        favoritesPageBuilder.button(j, new SimpleButton(head.getItem(), ice -> {
                            handleClick(player, head, ice);

                            // Update favorites after removing the head
                            HeadAPI.getFavoriteHeads(player.getUniqueId()).thenAcceptAsync(updated -> {
                                if (!updated.isEmpty()) {
                                    favorites(player, page); // Refresh the GUI
                                } else {
                                    mainGui.open(player);
                                }
                            }, Utils.SYNC);
                        }));
                    }

                    favoritesGui.addPage(favoritesPageBuilder.build());
                }

                favoritesGui.open(player, !favoritesGui.getPages().isEmpty() ? page : 0);
            } else {
                localization.sendMessage(player, "noFavorites");
                Sounds.FAIL.play(player);
            }
        }, Utils.SYNC);
    }

    public static void prebuildCategoryGuis() {
        // Prebuild category guis
        for (Category category : Category.VALUES) {
            List<Head> heads = HeadAPI.getHeads().stream().filter(head -> head.getCategory().orElse("N/A").equalsIgnoreCase(category.getName())).toList();
            GUI categoryGui = new SimpleGUI();

            // Iterate over the heads, chunking them into pages
            for (int i = 0; i < heads.size(); i += 45) {
                int end = Math.min(i + 45, heads.size());
                List<Head> section = heads.subList(i, end);

                PaginationBuilder categoryPageBuilder = new PaginationBuilder(categoryGui)
                        .parentGui(mainGui)
                        .name(ChatColor.GOLD + category.getDisplayName() + ChatColor.DARK_GRAY + " (" + heads.size() + ")");

                // Iterate over the heads in the current section and add them to the inventory
                for (int j = 0; j < section.size(); j++) {
                    Head head = section.get(j);
                    categoryPageBuilder.button(j, new SimpleButton(head.getItem(), e -> {
                        Player player = (Player) e.getWhoClicked();
                        if (categoryPermission && !player.hasPermission("headdb.category." + category.getName()) && !player.hasPermission("headdb.category.*")) {
                            localization.sendMessage(player, "noPermission");
                            return;
                        }

                        handleClick(player, head, e);
                    }));
                }

                // Build the page and add it to the category GUI
                categoryGui.addPage(categoryPageBuilder.build());
            }

            categoryGuis.put(category, categoryGui);
        }

        // Prebuild ALL gui.
        List<Head> heads = HeadAPI.getHeads();
        for (int i = 0; i < heads.size(); i += 45) {
            int end = Math.min(i + 45, heads.size());
            List<Head> section = heads.subList(i, end);
            PaginationBuilder allPageBuilder = new PaginationBuilder(allGui)
                    .parentGui(mainGui)
                    .name(ChatColor.GOLD + "All Heads" + ChatColor.DARK_GRAY + " (" + heads.size() + ")");

            // Iterate over the heads in the current section and add them to the inventory
            for (int j = 0; j < section.size(); j++) {
                Head head = section.get(j);
                // This is the slot for the current head, relative to the page (0-35)
                allPageBuilder.button(j, new SimpleButton(head.getItem(), e -> {
                    Player player = (Player) e.getWhoClicked();
                    if (categoryPermission && !player.hasPermission("headdb.category.all") && !player.hasPermission("headdb.category.*")) {
                        localization.sendMessage(player, "noPermission");
                        return;
                    }

                    handleClick(player, head, e);
                }));
            }

            // Build the page and add it to the category GUI
            allGui.addPage(allPageBuilder.build());
        }
    }

    public static void openSearch(List<Head> heads, String id, Player player) {
        GUI gui = new SimpleGUI();
        for (int i = 0; i < heads.size(); i += 45) {
            int end = Math.min(i + 45, heads.size());
            List<Head> section = heads.subList(i, end); // Get the sublist for the current page

            PaginationBuilder pageBuilder = new PaginationBuilder(gui)
                    .parentGui(mainGui)
                    .name(ChatColor.DARK_GRAY + "Search » " + ChatColor.GOLD + id);

            // Iterate over the heads in the current section and add them to the inventory
            for (int j = 0; j < section.size(); j++) {
                Head head = section.get(j);
                pageBuilder.button(j, new SimpleButton(head.getItem(), ice -> {
                    handleClick(player, head, ice);
                }));
            }

            // Build the page and add it to the local GUI
            gui.addPage(pageBuilder.build());
            gui.open(player);
        }
    }

    private static void handleClick(Player player, Head head, InventoryClickEvent ice) {
        if (ice.isLeftClick()) {
            Utils.purchaseHead(player, head, ice.isShiftClick() ? 64 : 1);  // Give the player the head item
        } else if (ice.isRightClick()) {
            PlayerData playerData = HeadDB.getInstance().getPlayerDatabase().getOrCreate(player.getUniqueId());
            if (playerData.getFavorites().contains(head.getId())) {
                playerData.getFavorites().remove(head.getId());
                localization.sendMessage(player, "removedFavorite", msg -> msg.replace("%name%", head.getName()));
                Sounds.FAVORITE_REMOVE.play(player);
            } else {
                playerData.getFavorites().add(head.getId());
                localization.sendMessage(player, "addedFavorite", msg -> msg.replace("%name%", head.getName()));
                Sounds.FAVORITE.play(player);
            }
        }
    }

}
