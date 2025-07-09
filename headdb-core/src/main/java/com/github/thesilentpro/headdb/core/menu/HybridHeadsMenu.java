package com.github.thesilentpro.headdb.core.menu;

import com.github.thesilentpro.grim.button.SimpleButton;
import com.github.thesilentpro.grim.gui.GUI;
import com.github.thesilentpro.grim.page.PaginatedSimplePage;
import com.github.thesilentpro.headdb.api.model.Head;
import com.github.thesilentpro.headdb.core.HeadDB;
import com.github.thesilentpro.headdb.core.storage.PlayerData;
import com.github.thesilentpro.headdb.core.util.Compatibility;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class HybridHeadsMenu extends PaginatedSimplePage {

    public HybridHeadsMenu(HeadDB plugin, GUI<Integer> gui, Component title, List<Head> heads, List<ItemStack> items) {
        super(gui, title, 6, 48, 49, 50);
        preventInteraction();
        for (Head head : heads) {
            addButton(new SimpleButton(head.getItem(), ctx -> {
                Player player = (Player) ctx.event().getWhoClicked();
                if (!ctx.event().getWhoClicked().hasPermission("headdb.category." + head.getCategory())) {
                    plugin.getLocalization().sendMessage(ctx.event().getWhoClicked(), "noPermission");
                    return;
                }

                if (ctx.event().getClick() == ClickType.RIGHT) {
                    PlayerData playerData = plugin.getPlayerStorage().getPlayer(player.getUniqueId());
                    if (playerData.getFavorites().contains(head.getId())) {
                        playerData.removeFavorite(head.getId());
                        plugin.getLocalization().sendMessage(ctx.event().getWhoClicked(), "menu.favorites.remove", msg -> msg.replaceText(builder -> builder.matchLiteral("{name}").replacement(head.getName())));
                    } else {
                        playerData.addFavorite(head.getId());
                        plugin.getLocalization().sendMessage(ctx.event().getWhoClicked(), "menu.favorites.add", msg -> msg.replaceText(builder -> builder.matchLiteral("{name}").replacement(head.getName())));
                    }
                    return;
                }

                if (plugin.getEconomyProvider() != null) {
                    new PurchaseHeadMenu(plugin, player, head, this).open(player);
                } else {
                    ItemStack item = head.getItem();
                    if (ctx.event().getClick() == ClickType.SHIFT_LEFT) {
                        item.setAmount(64);
                    }
                    player.getInventory().addItem(item);
                    plugin.getLocalization().sendMessage(player, "purchase.noEconomy", msg ->
                            msg.replaceText(builder -> builder.matchLiteral("{amount}").replacement(String.valueOf(item.getAmount())))
                                    .replaceText(builder -> builder.matchLiteral("{name}").replacement(head.getName()))
                    );
                }
            }));
        }
        for (ItemStack item : items) {
            addButton(new SimpleButton(item, ctx -> {
                if (ctx.event().getClick() == ClickType.RIGHT) {
                    PlayerData playerData = plugin.getPlayerStorage().getPlayer(ctx.event().getWhoClicked().getUniqueId());
                    UUID id = Compatibility.getIdFromItem(item);
                    if (playerData.getLocalFavorites().contains(id)) {
                        playerData.removeLocalFavorite(id);
                        plugin.getLocalization().sendMessage(ctx.event().getWhoClicked(), "menu.favorites.remove", msg -> msg.replaceText(builder -> builder.matchLiteral("{name}").replacement(Compatibility.getNameFromItem(item))));
                    } else {
                        playerData.addLocalFavorite(id);
                        plugin.getLocalization().sendMessage(ctx.event().getWhoClicked(), "menu.favorites.add", msg -> msg.replaceText(builder -> builder.matchLiteral("{name}").replacement(Compatibility.getNameFromItem(item))));
                    }
                    //reRender(ctx.event().getView());
                    return;
                }

                if (ctx.event().getClick() == ClickType.SHIFT_LEFT) {
                    item.setAmount(64);
                }
                ctx.event().getWhoClicked().getInventory().addItem(item);
            }));
        }
        reRender();
    }

}