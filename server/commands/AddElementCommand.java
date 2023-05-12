package commands;

import exceptions.NoSuchCommandException;
import exceptions.WrongAmountOfElementsException;
import util.ResponseCode;
import util.ServerResponse;
import util.TicketRaw;
import utility.CollectionManager;
import utility.Console;
import utility.OrganizationAsker;
import models.Ticket;

import java.time.LocalDate;

/**
 * Command 'add_element'. Saves the collection to a file.
 */
public class AddElementCommand extends AbstractCommand{
    private CollectionManager collectionManager;
    private OrganizationAsker organizationAsker;
    private LocalDate creationDate;


    public AddElementCommand(CollectionManager collectionManager, OrganizationAsker organizationAsker){
        super("add","add a new item to the collection");
        this.organizationAsker = organizationAsker;
        this.collectionManager = collectionManager;
        creationDate = LocalDate.now();

    }

    /**
     * Execute of 'add_element' command.
     */
    @Override
    public ServerResponse execute(String argument, Object object) throws NoSuchCommandException {
        try {
            if (!argument.isEmpty() || object == null) throw new WrongAmountOfElementsException();
            TicketRaw ticketRaw = (TicketRaw) object;
            collectionManager.addToCollection(new Ticket(
                    collectionManager.generateNextId(),
                    ticketRaw.getName(),
                    ticketRaw.getCoordinates(),
                    creationDate,
                    ticketRaw.getPrice(),
                    ticketRaw.getDiscount(),
                    ticketRaw.getRefundable(),
                    ticketRaw.getType(),
                    ticketRaw.getPerson()
            ));
            Console.println("Ticket is created");
        } catch (WrongAmountOfElementsException e) {
            Console.printerror("Used: " + getName());
            throw new RuntimeException(e);
        }
        return new ServerResponse("", ResponseCode.SUCCESS);
    }
}
