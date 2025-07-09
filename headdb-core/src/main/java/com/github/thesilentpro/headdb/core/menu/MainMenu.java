package com.github.thesilentpro.headdb.core.menu;

import com.github.thesilentpro.grim.button.Button;
import com.github.thesilentpro.grim.button.SimpleButton;
import com.github.thesilentpro.grim.page.Page;
import com.github.thesilentpro.grim.page.SimplePage;
import com.github.thesilentpro.headdb.api.model.Head;
import com.github.thesilentpro.headdb.core.HeadDB;
import com.github.thesilentpro.headdb.core.menu.gui.CustomCategoriesGUI;
import com.github.thesilentpro.headdb.core.menu.gui.HeadsGUI;
import com.github.thesilentpro.headdb.core.menu.gui.HybridHeadsGUI;
import com.github.thesilentpro.headdb.core.menu.gui.LocalHeadsGUI;
import com.github.thesilentpro.headdb.core.storage.PlayerData;
import com.github.thesilentpro.headdb.core.util.Compatibility;
import com.github.thesilentpro.inputs.paper.PaperInput;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MainMenu extends SimplePage {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainMenu.class);

    private final int[] ignoredSlots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33};

    public MainMenu(HeadDB plugin) {
        super(plugin.getLocalization().getConsoleMessage("menu.main.name").orElseGet(() -> Component.text("HeadDB").color(NamedTextColor.RED)), 6);
        preventInteraction();

        // Wait for database to update then setup menu
        plugin.getHeadApi().onReady().thenAcceptAsync(heads -> {
            // Preferred category order
            List<String> preferredOrder = List.of(
                    "Alphabet",
                    "Animals",
                    "Blocks",
                    "Decoration",
                    "Food & Drinks",
                    "Humanoid",
                    "Humans",
                    "Miscellaneous",
                    "Monsters",
                    "Plants"
            );

            // Index a sample head per category
            Map<String, Head> headByCategory = new HashMap<>();
            for (Head head : heads) {
                headByCategory.putIfAbsent(head.getCategory(), head);
            }

            List<String> orderedCategories = new ArrayList<>(preferredOrder.size() + headByCategory.size());
            Set<String> seen = new HashSet<>();

            // Add preferred categories (if available)
            for (String preferred : preferredOrder) {
                if (headByCategory.containsKey(preferred)) {
                    orderedCategories.add(preferred);
                    seen.add(preferred);
                }
            }

            // Add remaining categories not in preferred list
            for (String category : headByCategory.keySet()) {
                if (seen.add(category)) { // Set#add returns false if already added
                    orderedCategories.add(category);
                }
            }

            // add buttons
            int i = 0;
            for (String category : orderedCategories) {
                if (i >= ignoredSlots.length) {
                    break;
                }

                Head head = headByCategory.get(category);
                if (head == null) {
                    continue;
                }

                ItemStack item = Compatibility.setItemDetails(head.getItem(), plugin.getLocalization().getConsoleMessage("menu.main.category." + category.toLowerCase(Locale.ROOT) + ".name").orElseGet(() -> Component.text(category).color(NamedTextColor.GOLD)), Component.text(""));
                int slot = ignoredSlots[i++];
                setButton(slot, new SimpleButton(item, ctx -> {
                    if (!ctx.event().getWhoClicked().hasPermission("headdb.category." + category)) {
                        plugin.getLocalization().sendMessage(ctx.event().getWhoClicked(), "noPermission");
                        return;
                    }

                    HeadsGUI categoryGui = plugin.getMenuManager().get(category.replace(" ", "_").replace("&", "_"));
                    int pageIndex = 0;
                    if (plugin.getCfg().isTrackPage()) {
                        pageIndex = categoryGui.getGuiRegistry().getCurrentPage(ctx.event().getWhoClicked().getUniqueId(), categoryGui.getKey()).orElse(0);
                    }
                    categoryGui.open((Player) ctx.event().getWhoClicked(), pageIndex);
                }));
            }

            // Local heads
            ItemStack localItem = plugin.getHeadApi()
                    .findByTexture("7f6bf958abd78295eed6ffc293b1aa59526e80f54976829ea068337c2f5e8")
                    .join()
                    .map(head -> Compatibility.setItemDetails(head.getItem(), plugin.getLocalization().getConsoleMessage("menu.main.local.name")
                            .orElseGet(() -> Component.text("Local Heads").color(NamedTextColor.AQUA)), Component.text("")))
                    .orElseGet(() -> Compatibility.newItem(Material.COMPASS, plugin.getLocalization().getConsoleMessage("menu.main.local.name")
                            .orElseGet(() -> Component.text("Local Heads").color(NamedTextColor.AQUA)), Component.text(""))
                    );
            setButton(41, new SimpleButton(localItem, ctx -> {
                if (!ctx.event().getWhoClicked().hasPermission("headdb.category.local")) {
                    plugin.getLocalization().sendMessage(ctx.event().getWhoClicked(), "noPermission");
                    return;
                }

                List<ItemStack> localItems = plugin.getHeadApi().computeLocalHeads();
                if (localItems.isEmpty()) {
                    plugin.getLocalization().sendMessage(ctx.event().getWhoClicked(), "localNone");
                    return;
                }

                LocalHeadsGUI localGui = new LocalHeadsGUI(plugin, "local_" + ctx.event().getWhoClicked().getUniqueId().toString(), plugin.getLocalization().getConsoleMessage("menu.local.name").orElseGet(() -> Component.text("HeadDB » Local").color(NamedTextColor.GOLD)), localItems);
                int pageIndex = 0;
                if (plugin.getCfg().isTrackPage()) {
                    pageIndex = localGui.getGuiRegistry().getCurrentPage(ctx.event().getWhoClicked().getUniqueId(), localGui.getKey()).orElse(0);
                }
                localGui.open((Player) ctx.event().getWhoClicked(), pageIndex);
            }));

            // Info item
            if (plugin.getCfg().isShowInfoItem()) {
                Component[] infoLore = new Component[]{
                        Component.text("❓ Didn't spot the perfect head in our collection?")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.YELLOW),
                        Component.text("🎯 We're always adding more — and you can help!")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.YELLOW),
                        Component.text(""),
                        Component.text("📥 Submit your favorite or original heads")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.YELLOW),
                        Component.text("✨ Directly through our community Discord!")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.YELLOW),
                        Component.text(""),
                        Component.text("🔗 Discord > https://discord.gg/RJsVvVd")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.YELLOW)
                };
                ItemStack infoItem = plugin.getHeadApi()
                        .findByTexture("16439d2e306b225516aa9a6d007a7e75edd2d5015d113b42f44be62a517e574f")
                        .join()
                        .map(head -> Compatibility.setItemDetails(head.getItem(), Component.text("Can't find the head you're looking for?").color(NamedTextColor.RED), infoLore))
                        .orElseGet(() -> Compatibility.newItem(Material.BOOK, Component.text("Can't find the head you're looking for?").color(NamedTextColor.RED), infoLore));
                setButton(53, new SimpleButton(infoItem, ctx -> {
                    Compatibility.sendMessage(ctx.event().getWhoClicked(), Component.text("Click to join: https://discord.gg/RJsVvVd").color(NamedTextColor.AQUA));
                }));
            }

            // Favorites
            ItemStack favoritesItem = plugin.getHeadApi()
                    .findByTexture("76fdd4b13d54f6c91dd5fa765ec93dd9458b19f8aa34eeb5c80f455b119f278")
                    .join()
                    .map(head -> Compatibility.setItemDetails(head.getItem(), plugin.getLocalization().getConsoleMessage("menu.main.favorites.name")
                            .orElseGet(() -> Component.text("Favorites").color(NamedTextColor.YELLOW)), Component.text("")))
                    .orElseGet(() -> Compatibility.newItem(Material.BOOK, plugin.getLocalization().getConsoleMessage("menu.main.favorites.name")
                            .orElseGet(() -> Component.text("Favorites").color(NamedTextColor.YELLOW)), Component.text(""))
                    );
            setButton(42, new SimpleButton(favoritesItem, ctx -> {
                if (!ctx.event().getWhoClicked().hasPermission("headdb.category.favorites")) {
                    plugin.getLocalization().sendMessage(ctx.event().getWhoClicked(), "noPermission");
                    return;
                }

                UUID playerId = ctx.event().getWhoClicked().getUniqueId();
                PlayerData playerData = plugin.getPlayerStorage().getPlayer(playerId);

                List<Integer> favoriteIds = playerData.getFavorites();
                List<UUID> localFavoriteUUIDs = playerData.getLocalFavorites();

                // 1. Load official HeadDB heads
                List<CompletableFuture<Optional<Head>>> officialFutures = favoriteIds.stream()
                        .map(id -> plugin.getHeadApi().findById(id))
                        .toList();

                // 2. Convert local favorites (UUIDs) into ItemStacks
                List<ItemStack> localItems = localFavoriteUUIDs.stream()
                        .map(plugin.getHeadApi()::computeLocalHead)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList();

                // 3. Combine futures
                CompletableFuture
                        .allOf(officialFutures.toArray(new CompletableFuture[0]))
                        .thenApply(v ->
                                officialFutures.stream()
                                        .map(CompletableFuture::join)
                                        .filter(Optional::isPresent)
                                        .map(Optional::get)
                                        .toList()
                        )
                        .thenAcceptAsync(favoriteHeads -> {
                            if (favoriteHeads.isEmpty() && localItems.isEmpty()) {
                                plugin.getLocalization().sendMessage(ctx.event().getWhoClicked(), "favoritesNone");
                                return;
                            }

                            HybridHeadsGUI favoritesGui = new HybridHeadsGUI(plugin, "favorites_" + playerId.toString(), plugin.getLocalization().getConsoleMessage("menu.favorites.name").orElseGet(() -> Component.text("HeadDB » Favorites").color(NamedTextColor.GOLD)), favoriteHeads, localItems);
                            int pageIndex = 0;
                            if (plugin.getCfg().isTrackPage()) {
                                pageIndex = favoritesGui.getGuiRegistry().getCurrentPage(playerId, favoritesGui.getKey()).orElse(0);
                            }
                            favoritesGui.open((Player) ctx.event().getWhoClicked(), pageIndex);
                        }, Compatibility.getMainThreadExecutor(plugin))
                        .exceptionally(ex -> {
                            LOGGER.error("Failed to compute favorite heads for: {}", playerId, ex);
                            return null;
                        });
            }));

            // Custom categories
            ItemStack customCategoriesItem = plugin.getHeadApi()
                    .findByTexture(plugin.getCfg().getCustomCategoryTexture())
                    .join()
                    .map(head -> Compatibility.setItemDetails(head.getItem(), plugin.getLocalization().getConsoleMessage("menu.main.customCategories.name")
                            .orElseGet(() -> Component.text("More Categories").color(NamedTextColor.DARK_PURPLE)), Component.text("")))
                    .orElseGet(() -> Compatibility.newItem(plugin.getCfg().getCustomCategoryItem(), plugin.getLocalization().getConsoleMessage("menu.main.customCategories.name")
                            .orElseGet(() -> Component.text("More Categories").color(NamedTextColor.DARK_PURPLE)), Component.text(""))
                    );

            setButton(38, new SimpleButton(customCategoriesItem, ctx -> {
                if (!ctx.event().getWhoClicked().hasPermission("headdb.category.custom")) {
                    plugin.getLocalization().sendMessage(ctx.event().getWhoClicked(), "noPermission");
                    return;
                }

                CustomCategoriesGUI customCategoriesGui = plugin.getMenuManager().getCustomCategoriesGui();
                if (customCategoriesGui.getPages().isEmpty()) {
                    plugin.getLocalization().sendMessage(ctx.event().getWhoClicked(), "customCategoriesNone");
                    return;
                }
                int pageIndex = 0;
                if (plugin.getCfg().isTrackPage()) {
                    pageIndex = customCategoriesGui.getGuiRegistry().getCurrentPage(ctx.event().getWhoClicked().getUniqueId(), customCategoriesGui.getKey()).orElse(0);
                }
                customCategoriesGui.open((Player) ctx.event().getWhoClicked(), pageIndex);
            }));

            // Custom categories
            ItemStack searchItem = plugin.getHeadApi()
                    .findByTexture(plugin.getCfg().getSearchTexture())
                    .join()
                    .map(head -> Compatibility.setItemDetails(head.getItem(), plugin.getLocalization().getConsoleMessage("menu.main.search.name")
                            .orElseGet(() -> Component.text("Search").color(NamedTextColor.DARK_PURPLE)), Component.text("")))
                    .orElseGet(() -> Compatibility.newItem(plugin.getCfg().getSearchItem(), plugin.getLocalization().getConsoleMessage("menu.main.search.name")
                            .orElseGet(() -> Component.text("Search").color(NamedTextColor.GREEN)), Component.text(""))
                    );
            setButton(39, new SimpleButton(searchItem, ctx -> {
                if (!Compatibility.IS_PAPER) {
                    return; // Currently unsupposed, requires inputs to be updated for spigot support
                }

                HumanEntity entity = ctx.event().getWhoClicked();
                entity.closeInventory();
                plugin.getLocalization().sendMessage(entity, "command.search.input");
                PaperInput.awaitString()
                        .then((input, event) -> {
                            event.setCancelled(true);
                            Compatibility.getMainThreadExecutor(plugin).execute(() -> ((Player) entity).performCommand("hdb search " + input));
                        }).register(entity.getUniqueId());
            }));

            fill(this, new SimpleButton(Compatibility.newItem(Material.BLACK_STAINED_GLASS_PANE, Component.text(""))));
            reRender();
        }, Compatibility.getMainThreadExecutor(plugin));

    }

    private static void fill(Page page, Button button) {
        for (int i = 0; i < page.getSize(); i++) {
            if (page.getButton(i).isEmpty()) {
                page.setButton(i, button);
            }
        }
    }

}