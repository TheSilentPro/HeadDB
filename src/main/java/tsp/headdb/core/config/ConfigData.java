package tsp.headdb.core.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tsp.headdb.api.model.Category;
import tsp.headdb.api.HeadAPI;
import tsp.headdb.api.model.Head;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TheSilentPro (Silent)
 */
public class ConfigData {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigData.class);

    private boolean requireCategoryPermission;
    private boolean preloadHeads;
    private boolean includeLore;
    private boolean moreInfo;

    private boolean economyEnabled;
    private String economyProvider;
    private double defaultCost;
    private double categoryDefaultCost;
    private double localCost;
    private final Map<String, Double> categoryCosts = new HashMap<>();
    private final Map<Head, Double> costs = new HashMap<>();

    public ConfigData(FileConfiguration config) {
        reload(config);
    }

    public void reload(FileConfiguration config) {
        this.requireCategoryPermission = config.getBoolean("requireCategoryPermissions");
        this.preloadHeads = config.getBoolean("preloadHeads");
        this.includeLore = config.getBoolean("includeLore");
        this.moreInfo = config.getBoolean("moreInfo");

        this.economyEnabled = config.getBoolean("economy.enabled");
        this.economyProvider = config.getString("economy.provider");
        this.defaultCost = config.getDouble("economy.defaultCost");
        this.categoryDefaultCost = config.getDouble("economy.defaultCategoryCost");
        this.localCost = config.getDouble("economy.localCost");

        for (Category category : Category.VALUES) {
            categoryCosts.put(category.getName(), config.getDouble("economy.categoryCost." + category.getName())); // Do not put default category cost as default value for this!
        }

        if (this.economyEnabled) {
            ConfigurationSection costSection = config.getConfigurationSection("economy.cost");
            if (costSection != null) {
                for (String key : costSection.getKeys(false)) {
                    try {
                        HeadAPI.getHeadById(Integer.parseInt(key)).join().ifPresentOrElse(head -> costs.put(head, config.getDouble("economy.cost." + key)), () -> LOGGER.warn("Failed to find head with id: {}", key));
                    } catch (NumberFormatException nfe) {
                        HeadAPI.getHeadByTexture(key).join().ifPresentOrElse(head -> costs.put(head, config.getDouble("economy.cost." + key)), () -> LOGGER.warn("Failed to find head with texture: {}", key));
                    }
                }
            } else {
                LOGGER.warn("No cost section defined in the configuration.");
            }
        }
    }

    public boolean isEconomyEnabled() {
        return economyEnabled;
    }

    public String getEconomyProvider() {
        return economyProvider;
    }

    public double getLocalCost() {
        return localCost;
    }

    public double getDefaultCost() {
        return defaultCost;
    }

    public double getCategoryDefaultCost() {
        return categoryDefaultCost;
    }

    public Map<String, Double> getCategoryCosts() {
        return categoryCosts;
    }

    public Map<Head, Double> getCosts() {
        return costs;
    }

    public boolean shouldPreloadHeads() {
        return preloadHeads;
    }

    public boolean shouldRequireCategoryPermission() {
        return requireCategoryPermission;
    }

    public boolean shouldIncludeLore() {
        return includeLore;
    }

    public boolean shouldIncludeMoreInfo() {
        return moreInfo;
    }

}