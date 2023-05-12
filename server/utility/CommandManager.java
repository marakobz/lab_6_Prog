package utility;

import commands.*;
import util.ClientRequest;
import util.ServerResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Operates the commands.
 */
public class CommandManager {
    private final Map<String, AbstractCommand> commands = new HashMap<>();

    public CommandManager(CollectionManager collectionManager, UserIO userIO, OrganizationAsker organizationAsker, String filename){
        AddElementCommand addElementCommand = new AddElementCommand(collectionManager, organizationAsker);
        commands.put(addElementCommand.getName(), addElementCommand);
        AddIfMinCommand addIfMinCommand = new AddIfMinCommand(collectionManager,organizationAsker);
        commands.put(addIfMinCommand.getName(), addIfMinCommand);
        AverageOfDiscountCommand averageOfDiscountCommand = new AverageOfDiscountCommand(collectionManager);
        commands.put(averageOfDiscountCommand.getName(), averageOfDiscountCommand);
        ClearCommand clearCommand = new ClearCommand(collectionManager);
        commands.put(clearCommand.getName(), clearCommand);
        ExecuteScriptCommand executeScriptCommand = new ExecuteScriptCommand(userIO);
        commands.put(executeScriptCommand.getName(), executeScriptCommand);
        ExitCommand exitCommand = new ExitCommand(collectionManager);
        commands.put(exitCommand.getName(), exitCommand);
        GroupCountingCommand groupCountingCommand = new GroupCountingCommand(collectionManager);
        commands.put(groupCountingCommand.getName(), groupCountingCommand);
        HeadCommand headCommand = new HeadCommand(collectionManager);
        commands.put(headCommand.getName(), headCommand);
        HelpCommand helpCommand = new HelpCommand();
        commands.put(helpCommand.getName(), helpCommand);
        InfoCommand infoCommand = new InfoCommand(collectionManager);
        commands.put(infoCommand.getName(), infoCommand);
        PrintUniquePersonCommand printUniqiePersonCommand = new PrintUniquePersonCommand(collectionManager);
        commands.put(printUniqiePersonCommand.getName(), printUniqiePersonCommand);
        RemoveByIdCommand removeByIdCommand = new RemoveByIdCommand(collectionManager);
        commands.put(removeByIdCommand.getName(), removeByIdCommand);
        RemoveGreaterCommand removeGreaterCommand = new RemoveGreaterCommand(collectionManager, organizationAsker);
        commands.put(removeGreaterCommand.getName(), removeGreaterCommand);
        SaveCommand saveCommand = new SaveCommand(collectionManager);
        commands.put(saveCommand.getName(), saveCommand);
        ShowCommand showCommand = new ShowCommand(collectionManager);
        commands.put(showCommand.getName(), showCommand);
        UpdateCommand updateCommand = new UpdateCommand(collectionManager, organizationAsker);
        commands.put(updateCommand.getName(), updateCommand);
    }

    /**
     * @return list of commands.
     */
    public Map<String, AbstractCommand> getCommands() {
        return commands;
    }

    public ServerResponse process(ClientRequest clientRequest){
        return commands.get(clientRequest.getCommandName()).execute(clientRequest.getCommandArguments(), clientRequest.getObjectArgument());
    }

    @Override
    public String toString() {
        return "CommandManager for working with commands";
    }
}

