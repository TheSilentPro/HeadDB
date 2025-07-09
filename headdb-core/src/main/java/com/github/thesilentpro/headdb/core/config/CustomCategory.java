package com.github.thesilentpro.headdb.core.config;

import com.github.thesilentpro.headdb.api.model.Head;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CustomCategory {

    private String identifier;
    private boolean enabled;
    private String name;
    private ItemStack icon;
    private final List<Head> heads;

    public CustomCategory(String identifier, boolean enabled, String name, ItemStack icon, List<Head> heads) {
        this.identifier = identifier;
        this.enabled = enabled;
        this.name = name;
        this.icon = icon;
        this.heads = heads;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public void addHead(Head head) {
        this.heads.add(head);
    }

    public void removeHead(Head head) {
        this.heads.remove(head);
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public List<Head> getHeads() {
        return heads;
    }

}