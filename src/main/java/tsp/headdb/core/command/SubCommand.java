package tsp.headdb.core.command;

import tsp.headdb.HeadDB;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

// args[0] = sub-command | args[1+] = params
public abstract class SubCommand extends HeadDBCommand {

    private final String[] aliases;

    public SubCommand(String name, Collection<String> completions, @Nullable String... aliases) {
        super(name, "headdb.command." + name, completions);
        this.aliases = aliases;
    }

    public SubCommand(String name, String... aliases) {
        this(name, new ArrayList<>(), aliases);
    }

    public SubCommand(String name) {
        this(name, (String[]) null);
    }

    public Optional<String[]> getAliases() {
        return Optional.ofNullable(aliases);
    }

    public void register() {
        HeadDB.getInstance().getCommandManager().register(this);
    }

}
