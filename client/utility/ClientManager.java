package utility;

import exceptions.*;
import models.*;
import util.ClientRequest;
import util.ServerResponse;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.PriorityQueue;
import java.util.Scanner;

import static java.time.LocalDateTime.parse;



/**
 * manages all actions to run Client
 */
public class ClientManager {
    private InetAddress host;
    private int port;
    private int reconnectionTimeout;
    private int reconnectionAttempts;
    private int maxReconnectionAttempts;
    private final Handler handler;
    private SocketChannel socketChannel;
    private SocketAddress address;

    private final Requester requester;
    public File save_file = new File("temp.txt");
    private final PriorityQueue<Ticket> collection = new PriorityQueue<>();




    public ClientManager(int port, int reconnectionTimeout, int maxReconnectionAttempts, Handler handler, Requester requester) {
        this.port = port;
        this.reconnectionTimeout = reconnectionTimeout;
        this.maxReconnectionAttempts = maxReconnectionAttempts;
        this.handler = handler;
        this.requester = requester;
    }

    /**
     * Begins client operation.
     */
    public void run() {
        try {

            while (true) {
                try {
                    connectToServer();
                    if (socketChannel.isOpen()){
                        processRequestToServer();

                    }else {
                        System.exit(0);
                    }


                } catch (ConnectionErrorException exception) {
                    if (reconnectionAttempts >= maxReconnectionAttempts) {
                        Console.printerror("Exceeded the number of connection attempts.");
                        break;
                    }
                    try {
                        Thread.sleep(reconnectionTimeout);
                    } catch (IllegalArgumentException timeoutException) {
                        Console.printerror("The connection timeout" + reconnectionTimeout + " is beyond the limits of possible values");
                        Console.println("Reconnection with server will be as fast as possible");
                    } catch (Exception timeoutException) {
                        Console.printerror("Mistake occurred while trying to reconnect");
                        Console.println("Reconnection with server will be as fast as possible");
                    }
                }
                reconnectionAttempts++;
            }
            if (socketChannel != null) socketChannel.close();
            Console.println("Work of client is ended");
        } catch (NotDeclaredLimitsException exception) {
            Console.printerror("Client cannot be started");
        } catch (IOException exception) {
            Console.printerror("Mistake occurred while trying to end work of server");
        }
    }

    /**
     * Connecting to server.
     */
    private void connectToServer() throws ConnectionErrorException, NotDeclaredLimitsException {
        try {
            if (reconnectionAttempts >= 1) Console.println("Reconnecting with server");
            socketChannel = SocketChannel.open();

            Console.println("Connecting to server...");
            Console.println("Waiting for permission to exchange data");
            address = new InetSocketAddress(host, port);
            socketChannel.connect(address);

            Console.println("Permission to exchange data has been received.");


            if (!save_file.exists()){
                save_file.createNewFile();
            }else {
                if (save_file.length() == 0){
                    Console.println("Temp file is empty");
                }else{
                    askToLoad();
                }
            }if(!save_file.exists()){
                System.out.println("Temp file does not exist yet");
            }
            Console.println("Data loaded successfully");

        } catch (IllegalArgumentException exception) {
            Console.printerror("Address of server entered incorrectly.");
            throw new NotDeclaredLimitsException();
        } catch (IOException exception) {
            Console.printerror("Mistake occurred while trying to connect to server");
            throw new ConnectionErrorException();
        }
    }

    /**
     * Ask to load data from temporary file
     */
    public void askToLoad() throws IOException {
        while (true) {
            try {
                Scanner scanner = new Scanner(System.in);
                System.out.print("Do you want to load unsaved data from temporary file? (yes/no): ");
                String input = scanner.nextLine().trim().toLowerCase();
                if (!input.equals("yes") && !input.equals("no")) throw new NotDeclaredLimitsException();

                if (input.equals("yes")) {
                    loadFromTempFile();
                    Console.println("Data is loaded from temp file");
                } else {
                    while (true) {
                        try {
                            Console.println("Data from temp file is not loaded");
                            Console.println("Do you want to delete temp file? (yes/no)");
                            String answer = scanner.nextLine().trim().toLowerCase();
                            if (answer.equals("yes")) {
                                save_file.delete();
                                Console.println("Temporary file deleted");
                                save_file.createNewFile();
                            }
                            Console.println("Temporary file is not deleted");
                            if (!answer.equals("yes") && !answer.equals("no")) throw new NotDeclaredLimitsException();
                            break;
                        }catch (NotDeclaredLimitsException exception) {
                            Console.printerror("Answer has to 'yes' or 'no'");
                        }
                    }
                }

                if (!input.equals("yes") && !input.equals("no")) throw new NotDeclaredLimitsException();
                break;
            } catch (NotDeclaredLimitsException exception) {
                Console.printerror("Answer has to 'yes' or 'no'");
            }
        }
    }

    /**
     * Loads data from temporary file if its exists
     */
    private void loadFromTempFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(save_file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Ticket ticket = convertStringToTicket(line);
                    collection.add(ticket);
                    Console.println("Data is added from temporary file to 'test.csv'");
                } catch (IllegalArgumentException e) {
                    System.err.println("Incorrect format of data");
                    System.err.println("Data is not saved from temporary file");
                }
            }

        } catch (IOException e) {
            System.err.printf("An error occurred while reading from file '%s': %s%n", save_file.getName(), e.getMessage());
        }
    }

    private Ticket convertStringToTicket(String string) throws IllegalArgumentException {
        String[] fields = string.split(",");
        if (fields.length != 13) {
            throw new IllegalArgumentException("Invalid string format for ticket: " + string);
        }
        int id = Integer.parseInt(fields[0]);
        String name = fields[1];
        Float x = Float.parseFloat(fields[2]);
        float y = Float.parseFloat(fields[3]);
        Coordinates coordinates = new Coordinates(x, y);
        LocalDate creationDate = LocalDate.parse(fields[4]);

        int price = Integer.parseInt(fields[5]);
        long discount = Long.parseLong(fields[6]);
        boolean refundable = Boolean.parseBoolean(fields[7]);

        TicketType type = TicketType.valueOf(fields[8]);

        LocalDateTime birthday = parse(fields[9]);
        Float height = Float.parseFloat(fields[10]);
        Float weight = Float.parseFloat(fields[11]);
        Country nationality = Country.valueOf(fields[12]);


        Person person = new Person(birthday, height, weight, nationality);
        return new Ticket(id, name, coordinates, creationDate, price, discount, refundable, type, person);
    }

    /**
     * Server's request process.
     *
     * @return
     */
    private void processRequestToServer() throws IOException {
        ClientRequest requestToServer = new ClientRequest();
        ServerResponse serverResponse = null;

            do {

                try {

                    requestToServer = serverResponse != null ? handler.handle(serverResponse.getResponseCode()) :
                            handler.handle(null);

                    if (requestToServer.isEmpty()) continue;

                    ByteArrayOutputStream serverWriter = new ByteArrayOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(serverWriter);
                    objectOutputStream.writeObject(requestToServer);

                    byte[] bytes;
                    bytes = serverWriter.toByteArray();
                    ByteBuffer buffer = ByteBuffer.allocate(4096);
                    buffer.put(bytes);
                    buffer.flip();
                    serverWriter.write(bytes);

                    socketChannel.write(buffer);

                    ByteBuffer receiveBuffer = ByteBuffer.allocate(8192);
                    receiveBuffer.flip();
                    byte[] data = new byte[receiveBuffer.limit()];
                    receiveBuffer.get(data);

                    socketChannel.read(receiveBuffer);

                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.array());
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

                    objectInputStream.readObject();


                    serverResponse = requester.handle(requestToServer);


                    Console.print(serverResponse.getMessage());


            } catch(InvalidClassException | NotSerializableException exception){
                Console.printerror("Mistake occurred while sending data to server");
            } catch(ClassNotFoundException exception){
                    Console.printerror("Mistake occurred while reading given data");
                    Console.println("Server is disconnected, have to end client");
                    System.exit(0);
                }

        } while (!requestToServer.getCommandName().equals("exit")) ;
    }
}