package com.github.thesilentpro.headdb.core.command.sub;

import com.github.thesilentpro.headdb.core.HeadDB;
import com.github.thesilentpro.headdb.core.command.HDBSubCommand;
import com.github.thesilentpro.headdb.core.menu.gui.HeadsGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class HDBCommandOpen extends HDBSubCommand {

    private final HeadDB plugin;
    private List<String> completions;

    public HDBCommandOpen(HeadDB plugin) {
        super("open", "Open the database.", "[category]", "o");
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getLocalization().sendMessage(sender, "noConsole");
            return;
        }

        if (args.length == 1) {
            this.plugin.getMenuManager().getMainMenu().open(player);
            plugin.getLocalization().sendMessage(sender, "command.open.opening", msg -> msg.replaceText(builder -> builder.matchLiteral("{category}").replacement("Main")));
            return;
        }

        String category = String.join(" ", String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
        HeadsGUI categoryGui = plugin.getMenuManager().get(category.replace(" ", "_").replace("&", "_"));
        if (categoryGui == null) {
            plugin.getLocalization().sendMessage(sender, "command.open.invalidCategory", msg -> msg.replaceText(builder -> builder.matchLiteral("{category}").replacement(category)));
            return;
        }

        int pageIndex = 0;
        if (plugin.getCfg().isTrackPage()) {
            pageIndex = categoryGui.getGuiRegistry().getCurrentPage(player.getUniqueId(), categoryGui.getKey()).orElse(0);
        }
        categoryGui.open(player, pageIndex);
        plugin.getLocalization().sendMessage(sender, "command.open.opening", msg -> msg.replaceText(builder -> builder.matchLiteral("{category}").replacement(category)));
    }

    @Override
    public @Nullable List<String> handleCompletions(CommandSender sender, String[] args) {
        if (completions == null) {
            completions = plugin.getHeadApi().findKnownCategories();
        }
        return completions;
    }

}
