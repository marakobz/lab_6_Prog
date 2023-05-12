package commands;

import exceptions.WrongAmountOfElementsException;
import util.ResponseCode;
import util.ServerResponse;
import utility.CollectionManager;
import utility.Console;


/**
 * Command 'save'. Saves the collection to a file.
 */
public class SaveCommand extends AbstractCommand{
    private CollectionManager collectionManager;

    public SaveCommand(CollectionManager collectionManager) {
        super("save", "save collection to the file");
        this.collectionManager = collectionManager;
    }

    /**
     * Execute of 'save' command.
     */
    @Override
    public ServerResponse execute(String argument, Object object) {
        try {
            if (!argument.isEmpty()) throw new WrongAmountOfElementsException();
            collectionManager.saveCollection();
        } catch (WrongAmountOfElementsException exception) {
            Console.println("Used: '" + getName() + "'");
        }
        return new ServerResponse("", ResponseCode.SUCCESS);
    }


}
