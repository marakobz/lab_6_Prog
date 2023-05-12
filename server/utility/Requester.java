package utility;

import commands.AbstractCommand;
import commands.ICommand;
import exceptions.NoSuchCommandException;
import exceptions.WrongArgumentException;
import org.slf4j.Logger;
import util.ClientRequest;
import util.ResponseCode;
import util.ServerResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles requests.
 */
public class Requester {

    private final CommandManager commandManager;
    ServerResponse response;
    private Logger logger;


    public Requester(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    /**
     * Handles requests.
     *
     * @param request Request to be processed.
     * @return Response to request.
     */
    public ServerResponse handle(ClientRequest request) {
        executeCommand(
                request.getCommandName(),
                request.getCommandArguments(),
                request.getObjectArgument());
        return new ServerResponse("", ResponseCode.SUCCESS);
    }

    public ServerResponse executeCommand(String commandName, String argument, Object objectArgument){
        if (commandManager.getCommands().containsKey(commandName)){
            AbstractCommand abstractCommand = commandManager.getCommands().get(commandName);
            try {
                response = abstractCommand.execute(argument,objectArgument);
            } catch (NoSuchCommandException e){

                response = new ServerResponse("Unknown command detected: " + commandName, ResponseCode.ERROR);

            }
        } else {
            response = new ServerResponse("Unknown command detected: " + commandName, ResponseCode.ERROR);

        }
        return response;
    }


}