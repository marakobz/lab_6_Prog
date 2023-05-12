package utility;

import models.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;

import java.io.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

import static java.time.LocalDateTime.parse;


/**
 * Used to read and write data from the given file.
 */
public class CSVReader{

    private String filename;
    private Logger logger;
    public CSVReader(String filename, Logger logger){
        this.filename = filename;
        this.logger = logger;
    }

    /**
     * Read collection from the given file.
     */
    public PriorityQueue<Ticket> readFromFile() {

        try (InputStreamReader fileReader = new InputStreamReader(new FileInputStream(filename));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withHeader());) {
            PriorityQueue<Ticket> tickets = new PriorityQueue<>() ;
            for (CSVRecord fields : csvParser) {
                int id = Integer.parseInt(fields.get("id"));
                String name = fields.get("name");
                Float x = Float.parseFloat(fields.get("x"));
                float y = Float.parseFloat(fields.get("y"));
                Coordinates coordinates = new Coordinates(x, y);
                LocalDate creationDate = LocalDate.parse(fields.get("creationDate"));

                int price = Integer.parseInt(fields.get("price"));
                long discount = Long.parseLong(fields.get("discount"));
                boolean refundable = Boolean.parseBoolean(fields.get("refund"));

                TicketType type = TicketType.valueOf(fields.get("type"));

                LocalDateTime birthday = parse(fields.get("birthday"));
                Float height = Float.parseFloat(fields.get("height"));
                Float weight = Float.parseFloat(fields.get("weight"));
                Country nationality = Country.valueOf(fields.get("nationality"));


                Person person = new Person(birthday, height, weight, nationality);
                tickets.add(new Ticket(
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
            }

            return tickets;
        } catch (FileNotFoundException | IllegalArgumentException | DateTimeParseException e) {
            logger.error("Error reading ticket data file: " + e.getMessage() + ", check data in file.");
            logger.debug("Work of server is ended because collection contains wrong arguments.");
            System.exit(0);
        } catch (IOException e1) {
            logger.error(e1.getMessage());
        }

        return new PriorityQueue<>();
    }

    /**
     * Write collection to the given file.
     */
    public void write(PriorityQueue<Ticket> tickets) {
        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filename));
            CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT
                    .withHeader(
                            "id",
                            "name",
                            "x",
                            "y",
                            "creationDate",
                            "price",
                            "discount",
                            "refund",
                            "type",
                            "birthday",
                            "height",
                            "weight",
                            "nationality"
                    ));

            for (Ticket ticket : tickets) {
                csvPrinter.printRecord(
                        ticket.getId(),
                        ticket.getName(),
                        ticket.getCoordinates().getX(),
                        ticket.getCoordinates().getY(),
                        ticket.getCreationDate(),
                        ticket.getDiscount(),
                        ticket.getPrice(),
                        ticket.getRefundable(),
                        ticket.getType(),
                        ticket.getPerson().getBirthday(),
                        ticket.getPerson().getHeight(),
                        ticket.getPerson().getWeight(),
                        ticket.getPerson().getNationality()
                );
            }
            csvPrinter.flush();
            csvPrinter.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }
}
