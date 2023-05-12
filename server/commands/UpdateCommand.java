package commands;

import util.ResponseCode;
import util.ServerResponse;
import util.TicketRaw;
import utility.CollectionManager;
import utility.OrganizationAsker;
import exceptions.*;
import models.Coordinates;
import models.Person;
import models.Ticket;
import models.TicketType;
import java.time.LocalDate;

/**
 * This is command 'update'. Refreshes an element of collection which id equals given one.
 */
public class UpdateCommand extends AbstractCommand{
    CollectionManager collectionManager;
    OrganizationAsker organizationAsker;

    public UpdateCommand(CollectionManager collectionManager, OrganizationAsker organizationAsker){
        super("update", " ID - reload the value of the collection item");
        this.collectionManager = collectionManager;
        this.organizationAsker = organizationAsker;
    }

    /**
     * Execute of 'update' command.
     */

    @Override
    public ServerResponse execute(String argument, Object object){
        try {
            if (argument.isEmpty() || object == null) throw new WrongAmountOfElementsException();
            if (collectionManager.collectionSize() == 0) throw new CollectionIsEmptyException();

            var id = Integer.parseInt(argument);
            if (id <= 0) throw new NumberFormatException();
            var oldTicket = collectionManager.getById(id);

            if (oldTicket == null) throw new TicketNotFoundException();

            TicketRaw ticketRaw = (TicketRaw) object;
            String name =ticketRaw.getName() == null ? oldTicket.getName() : ticketRaw.getName();
            Coordinates coordinates = ticketRaw.getCoordinates() == null ? oldTicket.getCoordinates() : ticketRaw.getCoordinates();
            LocalDate creationDate = oldTicket.getCreationDate();
            int price = ticketRaw.getPrice() == -100 ? oldTicket.getPrice() :ticketRaw.getPrice();
            long discount = ticketRaw.getDiscount() == -100 ? oldTicket.getDiscount() : ticketRaw.getDiscount();
            Boolean refundable = ticketRaw.getRefundable() == null ? oldTicket.getRefundable() : ticketRaw.getRefundable();
            TicketType type = ticketRaw.getType() == null ? oldTicket.getType() : ticketRaw.getType();
            Person person = ticketRaw.getPerson() == null ? oldTicket.getPerson() : ticketRaw.getPerson();

            collectionManager.removeFromCollection(oldTicket);

            if (organizationAsker.askQuestion("Change name of ticket?")) name = organizationAsker.askName();
            if (organizationAsker.askQuestion("Change coordinates?")) coordinates = organizationAsker.askCoordinates();
            if (organizationAsker.askQuestion("Change price?")) price = organizationAsker.askPrice();
            if (organizationAsker.askQuestion("Change discount?")) discount = organizationAsker.askDiscount();
            if (organizationAsker.askQuestion("Change if ticket is refundable or not")) refundable = organizationAsker.askRefund("");
            if (organizationAsker.askQuestion("Change type of ticket?")) type = organizationAsker.askTicketType();
            if (organizationAsker.askQuestion("Change person?")) person = (Person) organizationAsker.askPerson();

            collectionManager.addToCollection(
                    new Ticket(
                    id,
                    name,
                    coordinates,
                    creationDate,
                    price,
                    discount,
                    refundable,
                    type,
                    person
            ));
            System.out.println("Ticket is changed");
        } catch (CollectionIsEmptyException |
                 WrongAmountOfElementsException |
                 TicketNotFoundException |
                 IncorrectScriptException e) {
            throw new RuntimeException(e);
        }
        return new ServerResponse("", ResponseCode.SUCCESS);
    }
}
