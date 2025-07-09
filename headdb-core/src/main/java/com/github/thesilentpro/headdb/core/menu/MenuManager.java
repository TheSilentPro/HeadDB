package com.github.thesilentpro.headdb.core.menu;

import com.github.thesilentpro.headdb.core.HeadDB;
import com.github.thesilentpro.headdb.core.config.CustomCategory;
import com.github.thesilentpro.headdb.core.menu.gui.CustomCategoriesGUI;
import com.github.thesilentpro.headdb.core.menu.gui.HeadsGUI;
import com.github.thesilentpro.headdb.core.util.Compatibility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class MenuManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuManager.class);
    private final MainMenu mainMenu;
    private CustomCategoriesGUI customCategoriesGui;
    private final Map<String, HeadsGUI> guis = new HashMap<>();

    public MenuManager(HeadDB plugin) {
        this.mainMenu = new MainMenu(plugin);
        this.customCategoriesGui = null;
    }

    public void registerDefaults(HeadDB plugin) {
        for (String knownCategory : plugin.getHeadApi().findKnownCategories()) {
            plugin.getHeadApi().findByCategory(knownCategory).thenAcceptAsync(heads -> {
                try {
                    String key = knownCategory.replace(" ", "_").replace("&", "_");
                    register(key, new HeadsGUI(plugin, key, plugin.getLocalization().getConsoleMessage("menu.category." + knownCategory.toLowerCase(Locale.ROOT)).orElseGet(() -> Component.text("HeadDB » " + knownCategory).color(NamedTextColor.GOLD)), heads));
                } catch (Throwable ex) {
                    LOGGER.error("Failed to register known category: {}", knownCategory, ex);
                }
            }, Compatibility.getMainThreadExecutor(plugin));
        }

        // Load custom categories
        List<CustomCategory> customCategories = plugin.getCfg().resolveCustomCategories().stream()
                .peek(category -> {
                    if (!category.isEnabled()) {
                        LOGGER.debug("Skipping disabled custom category: {}", category.getIdentifier());
                    }
                })
                .filter(CustomCategory::isEnabled)
                .peek(category -> register(category.getIdentifier(), new HeadsGUI(plugin, "custom_" + category.getIdentifier(), plugin.getLocalization().getConsoleMessage("menu.category." + category.getIdentifier()).orElseGet(() -> MiniMessage.miniMessage().deserialize("<red>HeadDB <gray>» " + category.getName())), category.getHeads())))
                .collect(Collectors.toList());

        customCategoriesGui = new CustomCategoriesGUI(plugin, "custom_categories", plugin.getLocalization().getConsoleMessage("menu.customCategories.name").orElseGet(() -> Component.text("HeadDB » More Categories").color(NamedTextColor.GOLD)), customCategories);
    }

    public void register(String key, HeadsGUI menu) {
        this.guis.put(key, menu);
    }

    public HeadsGUI get(String key) {
        return this.guis.get(key);
    }

    public CustomCategoriesGUI getCustomCategoriesGui() {
        return customCategoriesGui;
    }

    public MainMenu getMainMenu() {
        return this.mainMenu;
    }

}
