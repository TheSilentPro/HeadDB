package com.github.thesilentpro.headdb.core.menu;

import com.github.thesilentpro.headdb.api.model.Head;
import com.github.thesilentpro.grim.button.SimpleButton;
import com.github.thesilentpro.grim.page.Page;
import com.github.thesilentpro.grim.page.SimplePage;
import com.github.thesilentpro.grim.page.handler.context.ButtonClickContext;
import com.github.thesilentpro.headdb.core.HeadDB;
import com.github.thesilentpro.headdb.core.util.Compatibility;
import com.github.thesilentpro.inputs.paper.PaperInput;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.function.Consumer;

public class PurchaseHeadMenu extends SimplePage {

    private final HeadDB plugin;
    private final Head head;

    public PurchaseHeadMenu(HeadDB plugin, Player player, Head head, Page parentPage) {
        super(plugin.getLocalization().getMessage(player.getUniqueId(), "menu.purchase.name").orElseGet(() -> Component.text("HeadDB » " + head.getName() + " » Purchase")).replaceText(builder -> builder.matchLiteral("{name}").replacement(head.getName())), 6);
        this.plugin = plugin;
        this.head = head;
        preventInteraction();

        ItemStack item = head.getItem();
        setButton(13, new SimpleButton(item));

        double price = plugin.getCfg().getCategoryPrice(head.getCategory().toLowerCase(Locale.ROOT));

        ItemStack one = item.clone();
        Compatibility.setItemDetails(one, Component.text("Buy 1x").color(NamedTextColor.GOLD), Component.text("Cost: ").color(NamedTextColor.GRAY).append(Component.text(price).color(NamedTextColor.RED)));
        setButton(28, new SimpleButton(one, handlePurchase(1))); // x1

        ItemStack half = item.clone();
        half.setAmount(32);
        Compatibility.setItemDetails(half, Component.text("Buy 32x").color(NamedTextColor.GOLD), Component.text("Cost: ").color(NamedTextColor.GRAY).append(Component.text(price * 32).color(NamedTextColor.RED)));
        setButton(30, new SimpleButton(half, handlePurchase(32))); // x32

        ItemStack stack = item.clone();
        stack.setAmount(64);
        Compatibility.setItemDetails(stack, Component.text("Buy 64x").color(NamedTextColor.GOLD), Component.text("Cost: ").color(NamedTextColor.GRAY).append(Component.text(price * 64).color(NamedTextColor.RED)));
        setButton(32, new SimpleButton(stack, handlePurchase(64))); // x64

        ItemStack custom = item.clone();
        Compatibility.setItemDetails(custom, Component.text("Buy custom amount").color(NamedTextColor.GOLD), Component.text("Click and type in chat the amount you wish to buy.").color(NamedTextColor.GREEN));
        setButton(34, new SimpleButton(custom, ctx -> {
            if (!Compatibility.IS_PAPER) {
                return; // Currently unsupposed, requires inputs to be updated for spigot support
            }

            HumanEntity entity = ctx.event().getWhoClicked();
            entity.closeInventory();
            PaperInput.awaitInteger()
                    .mismatch((input, event) -> {
                        event.setCancelled(true);
                        Compatibility.getMainThreadExecutor(plugin).execute(() -> plugin.getLocalization().sendMessage(event.getPlayer(), "invalidNumber", msg -> msg.replaceText(builder -> builder.matchLiteral("{number}").replacement(input))));
                    })
                    .then((input, event) -> {
                        event.setCancelled(true);
                        handlePurchase(input).accept(ctx);
                    }).register(entity.getUniqueId());
        }));

        setButton(49, new SimpleButton(Compatibility.newItem(Material.BARRIER, Component.text("Cancel").color(NamedTextColor.RED)), ctx -> parentPage.open((Player) ctx.event().getWhoClicked())));
    }

    private Consumer<ButtonClickContext> handlePurchase(int amount) {
        return ctx -> {
            double price = plugin.getCfg().getCategoryPrice(head.getCategory().toLowerCase(Locale.ROOT)) * amount;
            plugin.getEconomyProvider().purchase((Player) ctx.event().getWhoClicked(), price).thenAcceptAsync(success -> {
                if (!success) {
                    plugin.getLocalization().sendMessage(ctx.event().getWhoClicked(), "purchase.invalidFunds");
                    return;
                }

                ItemStack item = head.getItem();
                item.setAmount(amount);
                ctx.event().getWhoClicked().getInventory().addItem(item);
                plugin.getLocalization().sendMessage(ctx.event().getWhoClicked(), "purchase.success", msg ->
                        msg.replaceText(builder -> builder.matchLiteral("{amount}").replacement(String.valueOf(amount)))
                                .replaceText(builder -> builder.matchLiteral("{name}").replacement(head.getName()))
                                .replaceText(builder -> builder.matchLiteral("{cost}").replacement(String.valueOf(price)))
                );
            }, Compatibility.getMainThreadExecutor(plugin));
        };
    }

}