package tsp.headdb.core.command;

import tsp.headdb.HeadDB;

import javax.annotation.Nullable;
import java.util.Optional;

// args[0] = sub-command | args[1+] = params
public abstract class SubCommand extends HeadDBCommand {

    private final String[] aliases;

    public SubCommand(String name, @Nullable String[] aliases) {
        super(name, "headdb.command." + name);
        this.aliases = aliases;
    }

    public SubCommand(String name) {
        this(name, null);
    }

    public Optional<String[]> getAliases() {
        return Optional.ofNullable(aliases);
    }

    public void register() {
        HeadDB.getInstance().getCommandManager().register(this);
    }

}
