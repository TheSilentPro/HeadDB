package tsp.headdb.core.command;

public abstract class SubCommand extends HeadDBCommand {

    public SubCommand(String name) {
        super(name, "headdb.command." + name);
    }

}
