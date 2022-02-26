package tsp.headdb.command;

import org.bukkit.command.CommandSender;
import tsp.headdb.HeadDB;
import tsp.headdb.util.Localization;

/**
 * An interface for a HeadDB sub-command
 *
 * @author TheSilentPro
 * @since 4.0.0
 */
public interface HeadSubCommand {

    void handle(CommandSender sender, String[] args);

    default Localization getLocalization() {
        return HeadDB.getInstance().getLocalization();
    }

}
