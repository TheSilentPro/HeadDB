package com.github.thesilentpro.headdb.api.model;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Head {

    int getId();

    String getName();

    String getTexture();

    String getCategory();

    List<String> getTags();

    ItemStack getItem();

}