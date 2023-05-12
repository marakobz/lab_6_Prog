package commands;

import util.ResponseCode;
import util.ServerResponse;
import util.TicketRaw;
import utility.CollectionManager;
import utility.Console;
import utility.OrganizationAsker;
import exceptions.WrongAmountOfElementsException;
import models.Ticket;

import java.time.LocalDate;

/**
 * Command 'add_if_min'. Saves the collection to a file.
 */
public class AddIfMinCommand extends AbstractCommand {
    CollectionManager collectionManager;
    OrganizationAsker organizationAsker;

    public AddIfMinCommand(CollectionManager collectionManager, OrganizationAsker organizationAsker) {
        super("add_if_min", "add a new item to the collection if its value is less than that of the smallest item in this collection");
        this.organizationAsker = organizationAsker;
        this.collectionManager = collectionManager;
    }

    /**
     * Execute of 'add_if_min' command.
     */
    @Override
    public ServerResponse execute(String argument, Object object) {
        try {
            if (!argument.isEmpty()|| object == null) throw new WrongAmountOfElementsException();
            TicketRaw ticketRaw = (TicketRaw) object;
            var ticket = new Ticket(
                    collectionManager.generateNextId(),
                    ticketRaw.getName(),
                    ticketRaw.getCoordinates(),
                    LocalDate.now(),
                    ticketRaw.getPrice(),
                    ticketRaw.getDiscount(),
                    ticketRaw.getRefundable(),
                    ticketRaw.getType(),
                    ticketRaw.getPerson()
            );
            if (collectionManager.collectionSize() == 0 || ticket.compareTo(collectionManager.getFirst()) < 0) {
                collectionManager.addToCollection(ticket);
                Console.println("Ticket is added successfully");
            } else
                Console.printerror("The value of the ticket is greater than the value of the smallest of the tickets");
        } catch (WrongAmountOfElementsException exception) {
            Console.println("Used: '" + getName() + "'");
        }
        return new ServerResponse("", ResponseCode.SUCCESS);
    }
}
