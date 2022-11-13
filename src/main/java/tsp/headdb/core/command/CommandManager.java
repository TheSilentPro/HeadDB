package tsp.headdb.core.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandManager {

    private final Map<String, SubCommand> commands = new HashMap<>();

    public void register(SubCommand command) {
        this.commands.put(command.getName(), command);
    }

    public Optional<SubCommand> getCommand(String name) {
        SubCommand command = commands.get(name);
        if (command != null) {
            return Optional.of(command);
        }

        return getCommandByAlias(name);
    }

    public Optional<SubCommand> getCommandByAlias(String alias) {
        for (SubCommand entry : commands.values()) {
            if (entry.getAliases().isPresent()) {
                if (Arrays.stream(entry.getAliases().get()).anyMatch(name -> name.equalsIgnoreCase(alias))) {
                    return Optional.of(entry);
                }
            }
        }

        return Optional.empty();
    }

    public Map<String, SubCommand> getCommandsMap() {
        return commands;
    }
}
