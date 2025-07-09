package com.github.thesilentpro.headdb.core.command.sub;

import com.github.thesilentpro.headdb.core.HeadDB;
import com.github.thesilentpro.headdb.core.command.HDBSubCommand;
import com.github.thesilentpro.headdb.core.util.Compatibility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HDBCommandInfo extends HDBSubCommand {

    private final HeadDB plugin;

    public HDBCommandInfo(HeadDB plugin) {
        super("info", "Plugin information.", null, "i");
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");
        String javaVendor = System.getProperty("java.vendor");
        String javaVersion = System.getProperty("java.version");
        String bukkitName = Bukkit.getName();
        String bukkitVersion = Bukkit.getBukkitVersion().substring(0, Bukkit.getBukkitVersion().indexOf("-"));
        String serverVersion = Bukkit.getVersion().substring(0, Bukkit.getVersion().indexOf("(") - 1);
        int protocolVersion = -1;
        String clientBrand = "Unknown";

        if (sender instanceof Player player) {
            if (Compatibility.IS_PAPER) {
                protocolVersion = player.getProtocolVersion();
                clientBrand = player.getClientBrandName() != null ? player.getClientBrandName() : "Unknown";
            }
        }

        // Build the colored & emoji-rich message
        Component message = Component.text()
                .append(Component.text("⚙ Running ").color(NamedTextColor.GRAY))
                .append(Component.text("HeadDB " + Compatibility.getPluginVersion(plugin)).color(NamedTextColor.GOLD))
                .appendNewline()
                .append(Component.text(" OS: ").color(NamedTextColor.GRAY))
                .append(Component.text(osName + " " + osVersion + " (" + osArch + ")").color(NamedTextColor.WHITE))
                .appendNewline()
                .append(Component.text(" Java: ").color(NamedTextColor.GRAY))
                .append(Component.text(javaVendor + " " + javaVersion).color(NamedTextColor.WHITE))
                .appendNewline()
                .append(Component.text(" Server: ").color(NamedTextColor.GRAY))
                .append(Component.text(bukkitName + " " + bukkitVersion).color(NamedTextColor.WHITE))
                .appendNewline()
                .append(Component.text(" Implementation: ").color(NamedTextColor.GRAY))
                .append(Component.text(serverVersion).color(NamedTextColor.WHITE))
                .build();

        if (sender instanceof Player) {
            message = message.appendNewline()
                    .append(Component.text(" Protocol Version: ").color(NamedTextColor.GRAY))
                    .append(Component.text(protocolVersion == -1 ? "Unknown" : String.valueOf(protocolVersion)).color(NamedTextColor.WHITE))
                    .appendNewline()
                    .append(Component.text(" Client Brand: ").color(NamedTextColor.GRAY))
                    .append(Component.text(clientBrand).color(NamedTextColor.WHITE));
        }

        Compatibility.sendMessage(sender, message);
    }

}
