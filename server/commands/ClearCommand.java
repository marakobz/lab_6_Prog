package commands;

import exceptions.NoSuchCommandException;
import exceptions.WrongAmountOfElementsException;
import util.ResponseCode;
import util.ServerResponse;
import utility.CollectionManager;
import utility.Console;

/**
 * Command 'clear'. Saves the collection to a file.
 */
public class ClearCommand extends AbstractCommand{
    private CollectionManager collectionManager;
    public ClearCommand(CollectionManager collectionManager) {
     super("clear", "clear collection");
     this.collectionManager = collectionManager;
    }

    /**
     * Execute of 'clear' command.
     */

    @Override
    public ServerResponse execute(String argument, Object object) throws NoSuchCommandException {
        try {
            if (!argument.isEmpty() || object!= null) throw new WrongAmountOfElementsException();
            collectionManager.clearCollection();
            Console.println("Collection is clear");
        } catch (WrongAmountOfElementsException exception) {
            Console.println("Used: '" + getName() + "'");
        }
        return new ServerResponse("", ResponseCode.SUCCESS);
    }
}