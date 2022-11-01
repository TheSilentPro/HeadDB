package tsp.headdb.core.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandManager {

    private final Map<String, SubCommand> commands = new HashMap<>();

    public void register(SubCommand command) {
        this.commands.put(command.getName(), command);
    }

    public Optional<SubCommand> getCommand(String name) {
        return Optional.ofNullable(commands.get(name));
    }

    public Map<String, SubCommand> getCommandsMap() {
        return commands;
    }
}
