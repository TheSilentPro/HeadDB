package tsp.headdb.api.model;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tsp.headdb.core.util.Utils;

import java.util.Objects;
import java.util.Optional;

/**
 * @author TheSilentPro (Silent)
 */
public class Head {

    private final int id;
    private final String name;
    private final String texture;
    private final String category;
    private final String publishDate;
    private final String[] tags;
    private final String[] contributors;
    private final String[] collections;

    protected ItemStack item;

    public Head(
            int id,
            @NotNull String name,
            @Nullable String texture,
            @Nullable String category,
            @Nullable String publishDate,
            @Nullable String[] tags,
            @Nullable String[] contributors,
            @Nullable String[] collections
    ) {
        Objects.requireNonNull(name, "Name must not be null!");

        this.id = id;
        this.name = name;
        this.texture = texture;
        this.category = category;
        this.publishDate = publishDate;
        this.tags = tags;
        this.contributors = contributors;
        this.collections = collections;
    }

    @NotNull
    public ItemStack getItem() {
        if (item == null) {
            if (texture != null) {
                item = new ItemStack(Utils.asItem(this));
            } else {
                item = new ItemStack(Material.PLAYER_HEAD);
            }
        }

        return item;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getTexture() {
        return Optional.ofNullable(texture);
    }

    public Optional<String> getCategory() {
        return Optional.ofNullable(category);
    }

    public Optional<String> getPublishDate() {
        return Optional.ofNullable(publishDate);
    }

    public Optional<String[]> getTags() {
        return Optional.ofNullable(tags);
    }

    public Optional<String[]> getContributors() {
        return Optional.ofNullable(contributors);
    }

    public Optional<String[]> getCollections() {
        return Optional.ofNullable(collections);
    }

}