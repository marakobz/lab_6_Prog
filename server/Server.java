import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;



public class Server {
    static Logger logger = LoggerFactory.getLogger(Server.class);

    public static final int PORT = 1921;

    public static void main(String[] args) throws IOException{
        logger.info("The server is running");

    var userIO = new UserIO(new Scanner(System.in), new PrintWriter(System.out));
    var organizationAsker = new OrganizationAsker(userIO);
    var csvReader = new CSVReader("test.csv", logger);
    var collectionManager = new CollectionManager(csvReader);

    var commandManager = new CommandManager(collectionManager, userIO, organizationAsker, "test.csv");

        Requester requester = new Requester(commandManager);
        ServerManager serverManager = new ServerManager(PORT, requester, logger);
        serverManager.run();
    }
}
