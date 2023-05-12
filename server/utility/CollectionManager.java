package utility;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import models.*;

/**
 * Operates the collection.
 */

public class CollectionManager {

    private PriorityQueue<Ticket> collection = new PriorityQueue<>();
    private final CSVReader csvReader;
    public File save_file = new File("temp.txt");
    private final Scanner scanner = new Scanner(System.in);


    public CollectionManager(CSVReader csvReader) throws IOException {
        this.csvReader = csvReader;
        loadCollection();
    }

    public void clearCollection() {
        collection.clear();
    }
    /**
     * @return The collection itself.
     */
    public PriorityQueue<Ticket> getCollection(){
        return collection;
    }

    /**
     * @return Size of the collection.
     */
    public int collectionSize(){
        return collection.size();
    }

    /**
     * @return The first element of the collection or null if collection is empty.
     */
   public Ticket getFirst(){
        if (collection.isEmpty()) return null;
        return collection.peek();
    }


    /**
     * @return A ticket by his ID or null if ticket isn't found.
     */
    public Ticket getById(int id) {
        for (Ticket element : collection) {
            if (element.getId() == id) return element;
        }
        return null;
    }

    /**
     * @param ticket A ticket which value will be found.
     * @return A marine by his value or null if marine isn't found.
     */
    public Ticket getByValue(Ticket ticket){
        for (Ticket tickett : collection){
            if (tickett.equals(ticket)) return tickett;
        }
        return null;
    }

    /**
     * Removes greater.
     */
    public void removeGreater(Ticket ticketToCompare) {
        collection.removeIf(ticket -> ticket.compareTo(ticketToCompare) > 0);
    }

    /**
     * Adds a new ticket to collection.
     * @param element A ticket to add.
     */
    public void addToCollection(Ticket element) {
        collection.add(element);
        saveToTemp(element);
    }

    /**
     * Removes a new ticket to collection.
     * @param element A marine to remove.
     */
    public void removeFromCollection(Ticket element) {
        collection.remove(element);
    }

    /**
     * Generates next ID. It will be (the bigger one + 1).
     * @return Next ID.
     */
    public int generateNextId() {

        HashSet<Integer> hashSetId = new HashSet<>();
        if (collection != null){
            for (Object i : collection) {
                hashSetId.add(((Ticket)i).getId());
            }
        }
        int id;
        do {
            id = new Random().nextInt(Integer.MAX_VALUE);;
        } while (!hashSetId.add(id));
        return id;
    }


    /**
     * Generates creation date.
     */
    public LocalDate generateCreationDate(){
        if (collection.isEmpty()) return LocalDate.now();
        return collection.peek().getCreationDate();
    }

    /**
     * Saves data to temporary file
     */
    public void saveToTemp(Ticket ticket){
        try (PrintWriter writer = new PrintWriter(new FileWriter(save_file, true))) {
            writer.println(convertTicketToString(ticket));
        } catch (IOException e) {
            System.err.printf("An error occurred while writing to file '%s': %s%n", save_file.getName(), e.getMessage());
        }

    }
    /**
     * Saves the collection to file.
     */
    public void saveCollection() {
        csvReader.write(collection);
    }

    private String convertTicketToString(Ticket ticket){
        return String.join(",",
                String.valueOf(ticket.getId()),
                ticket.getName(),
                String.valueOf(ticket.getCoordinates().getX()),
                String.valueOf(ticket.getCoordinates().getY()),
                String.valueOf(ticket.getCreationDate()),
                String.valueOf(ticket.getPrice()),
                String.valueOf(ticket.getDiscount()),
                String.valueOf(ticket.getRefundable()),
                String.valueOf(ticket.getType()),
                String.valueOf(ticket.getPerson().getBirthday()),
                String.valueOf(ticket.getPerson().getHeight()),
                String.valueOf(ticket.getPerson().getWeight()),
                String.valueOf(ticket.getPerson().getNationality())

                );
    }

    /**
     * Exits the program
     */
    public void exit() throws IOException {
        if (save_file.length() == 0){
            Console.println("Temp file is empty, work is ended");
            System.exit(0);
        }else {
            System.out.print("There are unsaved changes. Do you want to save them? (yes/no): ");
            String response = scanner.nextLine().trim().toLowerCase();
            switch (response) {
                case "yes":
                    Console.println("Data is saved from temporary file");
                    saveCollection();

                    break;
                case "no":
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }

            Console.println("Work of program is ended");
            System.exit(0);
        }
    }

    /**
     * Loads the collection from file.
     */
    public void loadCollection() throws IOException {
        collection = csvReader.readFromFile();
    }

    /**
     * Counting group by its creation date
     */
    public void groupCountingByCrDate(){
        HashMap<LocalDate, TreeSet<Ticket>> groupMap = new HashMap<>();
        for (Ticket i : collection){
            if (groupMap.get((i).getCreationDate()) == null){
                TreeSet<Ticket> x = new TreeSet<>();
                x.add(i);
                groupMap.put((i).getCreationDate(), x);
            } else groupMap.get((i).getCreationDate()).add(i);
        }
        for (Map.Entry<LocalDate, TreeSet<Ticket>> entry : groupMap.entrySet()){
            Console.println("Elements created in " + entry.getKey() + " :\n");
            entry.getValue().forEach(CollectionManager::display);
        }
    }

    /**
     * Display the info about created ticket
     */
    static void display(Ticket ticket) {
        Console.println("ID of ticket:" + ticket.getId());
        Console.println("Name of ticket:" + ticket.getName());
        Console.println("Creation date of ticket:" + ticket.getCreationDate());
        Console.println("Coordinates:" + ticket.getCoordinates());
        Console.println("Price:" + ticket.getPrice());
        Console.println("Discount:" + ticket.getDiscount());
        Console.println("Refund:" + ticket.getRefundable());
        Console.println("Type of ticket:" + ticket.getType());
        Console.println("Human's info:" + ticket.getPerson());
    }

    @Override
    public String toString() {
        if (collection.isEmpty()) return "There is nothing in collection";

        StringBuilder builder = new StringBuilder();
        for (Ticket ticket : collection) {
            builder.append(ticket);
            builder.append("\n");
        }
        return builder.toString();
    }
}
