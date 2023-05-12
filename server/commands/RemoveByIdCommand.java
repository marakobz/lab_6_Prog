package commands;

import exceptions.NoSuchCommandException;
import util.ResponseCode;
import util.ServerResponse;
import utility.CollectionManager;
import models.Ticket;
import utility.Console;

/**
 * Command 'remove_by_id'. Saves the collection to a file.
 */
public class RemoveByIdCommand extends AbstractCommand{
    CollectionManager collectionManager;

    public RemoveByIdCommand(CollectionManager collectionManager){
        super("remove_by_id","delete an item from the collection by its id");
        this.collectionManager = collectionManager;
    }

    /**
     * Execute of 'remove_by_id' command.
     */


    @Override
    public ServerResponse execute(String argument, Object object) {
        if (argument.isEmpty() || object != null) {
            throw new NoSuchCommandException();
        }
        try {
            if (collectionManager.collectionSize() == 0) {
                Console.println("Cannot remove object");
            }
            int id = Integer.parseInt(argument);
            Ticket ticketToRemove = collectionManager.getById(id);

            if (ticketToRemove == null) {
                Console.println("Cannot remove object");

            }
            collectionManager.removeFromCollection(ticketToRemove);
            Console.println("Ticket is deleted");
        } catch (NumberFormatException e) {
            Console.println("the argument must be a long number");
        }
        return new ServerResponse("", ResponseCode.SUCCESS);
    }

}
