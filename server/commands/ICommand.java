package commands;

import util.ServerResponse;

/**
 * Interface for all commands.
 */
public interface ICommand {

    String getName();
    ServerResponse execute(String commandArguments, Object objectArgument);

}