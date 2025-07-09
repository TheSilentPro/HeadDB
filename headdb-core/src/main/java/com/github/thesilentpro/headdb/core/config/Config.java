package com.github.thesilentpro.headdb.core.config;

import com.github.thesilentpro.headdb.api.model.Head;
import com.github.thesilentpro.headdb.core.HeadDB;
import com.github.thesilentpro.headdb.core.util.Compatibility;
import com.github.thesilentpro.headdb.implementation.Index;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class Config {

    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    private HeadDB plugin;
    private FileConfiguration data;

    private long playerStorageSaveInterval;
    private int databaseThreads, apiThreads;
    private boolean indexingEnabled, indexById, indexByTexture, indexByCategory, indexByTag;
    private boolean preloadHeads;
    private boolean trackPage;
    private boolean updaterEnabled;

    private boolean showInfoItem;
    private boolean headsMenuDividerEnabled;
    private int headsMenuRows, dividerRow;
    private Material headsMenuDividerMaterial;
    private String headsMenuDividerName;

    private String backTexture;
    private Material backItem;

    private String infoTexture;
    private Material infoItem;

    private String nextTexture;
    private Material nextItem;

    private String customCategoryTexture;
    private Material customCategoryItem;

    private String searchTexture;
    private Material searchItem;

    private String economyProvider;
    private Map<String, Double> categoryPrices;


    public Config(HeadDB plugin) {
        this.plugin = plugin;
        this.data = plugin.getConfig();
    }

    public void load() {
        this.playerStorageSaveInterval = data.getLong("storage.player.saveInterval", 1800L);
        this.updaterEnabled = data.getBoolean("updater", true);
        this.trackPage = data.getBoolean("trackPage", true);
        this.preloadHeads = data.getBoolean("preloadHeads", true);
        this.databaseThreads = data.getInt("database.threads", 1);
        this.apiThreads = data.getInt("database.apiThreads", 1);
        this.indexingEnabled = data.getBoolean("indexing.enabled", true);
        this.indexById = data.getBoolean("indexing.by.id", true);
        this.indexByCategory = data.getBoolean("indexing.by.category", true);
        this.indexByTexture = data.getBoolean("indexing.by.texture", true);
        this.indexByTag = data.getBoolean("indexing.by.tag", true);

        this.showInfoItem = data.getBoolean("showInfoItem", true);
        this.headsMenuDividerEnabled = data.getBoolean("headsMenu.divider.enabled", true);
        this.headsMenuRows = data.getInt("headsMenu.rows", 4);
        this.dividerRow = data.getInt("headsMenu.divider.row", 5);
        this.headsMenuDividerMaterial = Material.matchMaterial(data.getString("headsMenu.divider.item.material", "BLACK_STAINED_GLASS_PANE"));
        this.headsMenuDividerName = data.getString("headsMenu.divider.item.name", " ");

        backTexture = data.getString("controls.back.head", "e5da4847272582265bdaca367237c96122b139f4e597fbc6667d3fb75fea7cf6");
        backItem = Material.matchMaterial(data.getString("controls.back.item", "ARROW"));

        infoTexture = data.getString("controls.info.head", "93e5cb83cfdf42e9c4d8a3ecb4f889f6a5f418dce0a894c97e416a0eaf0d58");
        infoItem = Material.matchMaterial(data.getString("controls.info.item", "PAPER"));

        nextTexture = data.getString("controls.next.head", "62bfb7ed2bd9f1d1f85c3d6ffb1626f252c5ecfd79d51a3f56ebf8e0c3c91");
        nextItem = Material.matchMaterial(data.getString("controls.next.item", "ARROW"));

        customCategoryTexture = data.getString("headsMenu.customCategories.head", "62bfb7ed2bd9f1d1f85c3d6ffb1626f252c5ecfd79d51a3f56ebf8e0c3c91");
        customCategoryItem = Material.matchMaterial(data.getString("headsMenu.customCategories.item", "BOOKSHELF"));

        searchTexture = data.getString("headsMenu.search.head", "9d9cc58ad25a1ab16d36bb5d6d493c8f5898c2bf302b64e325921c41c35867");
        searchItem = Material.matchMaterial(data.getString("headsMenu.search.item", "SPYGLASS"));

        this.economyProvider = data.getString("economy.provider", "");
        if (!economyProvider.isEmpty() && !economyProvider.equalsIgnoreCase("NONE")) {
            this.categoryPrices = new HashMap<>();
            for (String category : this.data.getConfigurationSection("economy.cost.category").getKeys(false)) {
                this.categoryPrices.put(category, this.data.getDouble("economy.cost.category." + category));
            }
        }
    }

    public List<CustomCategory> resolveCustomCategories() {
        List<CustomCategory> categories = new ArrayList<>();
        File file = new File(plugin.getDataFolder(), "categories.yml");
        if (!file.exists()) {
            plugin.saveResource("categories.yml", false);
        }

        if (!file.exists()) {
            LOGGER.error("Could not find or create categories.yml!");
            return categories;
        }

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (String id : cfg.getKeys(false)) {
            String basePath = id + ".icon";
            String iconName = cfg.getString(basePath + ".name");
            if (iconName == null) {
                LOGGER.error("Missing '{}' in categories.yml for category '{}'", basePath + ".name", id);
                continue;
            }

            ItemStack icon = null;
            // 1) Try head-based icon
            String iconHead = cfg.getString(basePath + ".head");
            if (iconHead != null && !iconHead.isEmpty()) {
                Optional<Head> headOpt = plugin.getHeadApi().findByTexture(iconHead).join();
                if (headOpt.isPresent()) {
                    icon = Compatibility.setItemDetails(
                            headOpt.get().getItem(),
                            MiniMessage.miniMessage().deserialize(iconName),
                            Component.empty()
                    );
                } else {
                    LOGGER.warn("No head found for texture '{}' in category '{}'", iconHead, id);
                }
            }
            // 2) Fallback to material-based icon
            if (icon == null) {
                String iconItem = cfg.getString(basePath + ".item");
                if (iconItem != null && !iconItem.isEmpty()) {
                    Material mat = Material.matchMaterial(iconItem);
                    if (mat != null) {
                        icon = Compatibility.newItem(
                                mat,
                                MiniMessage.miniMessage().deserialize(iconName),
                                Component.empty()
                        );
                    } else {
                        LOGGER.error("Unknown material '{}' for category '{}'", iconItem, id);
                    }
                }
            }
            if (icon == null) {
                LOGGER.error("Could not load icon for category '{}'; check .head or .item under '{}'", id, basePath);
                continue;
            }

            List<Head> heads = new ArrayList<>();
            for (String headTexture : cfg.getStringList(id + ".heads")) {
                plugin.getHeadApi().findByTexture(headTexture).join().ifPresent(heads::add);
            }

            categories.add(new CustomCategory(id, cfg.getBoolean(id + ".enabled", false), iconName, icon, heads));
        }

        return categories;
    }

    @Nullable
    public Index[] resolveEnabledIndexes() {
        if (!this.indexingEnabled) {
            return null;
        }

        int size = 0;
        if (indexById) size++;
        if (indexByCategory) size++;
        if (indexByTexture) size++;
        if (indexByTag) size++;

        if (size == 0) {
            return null;
        }

        Index[] enabledIndexes = new Index[size];
        int index = 0;

        if (indexById) enabledIndexes[index++] = Index.ID;
        if (indexByCategory) enabledIndexes[index++] = Index.CATEGORY;
        if (indexByTexture) enabledIndexes[index++] = Index.TEXTURE;
        if (indexByTag) enabledIndexes[index] = Index.TAG;

        return enabledIndexes;
    }

    public double getCategoryPrice(String id) {
        return this.categoryPrices.getOrDefault(id, 0D);
    }

    public Map<String, Double> getCategoryPrices() {
        return categoryPrices;
    }

    public String getEconomyProvider() {
        return economyProvider;
    }

    public Material getSearchItem() {
        return searchItem;
    }

    public String getSearchTexture() {
        return searchTexture;
    }

    public Material getCustomCategoryItem() {
        return customCategoryItem;
    }

    public String getCustomCategoryTexture() {
        return customCategoryTexture;
    }

    public String getBackTexture() {
        return backTexture;
    }

    public Material getBackItem() {
        return backItem;
    }

    public String getInfoTexture() {
        return infoTexture;
    }

    public Material getInfoItem() {
        return infoItem;
    }

    public String getNextTexture() {
        return nextTexture;
    }

    public Material getNextItem() {
        return nextItem;
    }


    public long getPlayerStorageSaveInterval() {
        return playerStorageSaveInterval;
    }

    public boolean isShowInfoItem() {
        return showInfoItem;
    }

    public boolean isHeadsMenuDividerEnabled() {
        return headsMenuDividerEnabled;
    }

    public int getHeadsMenuRows() {
        return headsMenuRows;
    }

    public int getDividerRow() {
        return dividerRow;
    }

    public Material getHeadsMenuDividerMaterial() {
        return headsMenuDividerMaterial;
    }

    public String getHeadsMenuDividerName() {
        return headsMenuDividerName;
    }

    public boolean isUpdaterEnabled() {
        return updaterEnabled;
    }

    public int getDatabaseThreads() {
        return databaseThreads;
    }

    public int getApiThreads() {
        return apiThreads;
    }

    public boolean isTrackPage() {
        return trackPage;
    }

    public boolean isPreloadHeads() {
        return preloadHeads;
    }

    public boolean isIndexingEnabled() {
        return indexingEnabled;
    }

    public boolean isIndexByCategory() {
        return indexByCategory;
    }

    public boolean isIndexById() {
        return indexById;
    }

    public boolean isIndexByTag() {
        return indexByTag;
    }

    public boolean isIndexByTexture() {
        return indexByTexture;
    }

}