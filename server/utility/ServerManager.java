package utility;

import exceptions.ClosingSocketException;
import exceptions.ConnectionErrorException;
import exceptions.OpeningServerSocketException;
import org.slf4j.Logger;
import util.ClientRequest;
import util.ServerResponse;


import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

/*
TODO логирование с дебагером
TODO селекторы
TODO еще раз всё перероверить и залить на гелиос
 */

public class ServerManager {
    private int port;
    private int soTimeout;
    private ServerSocket serverSocket;
    private Requester requester;
    private Logger logger;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private SocketChannel clientSocket;



    public ServerManager(int port, Requester requester, Logger logger) {
        this.port = port;
        this.requester = requester;
        this.logger = logger;
    }

    /**
     * Begins server operation.
     *
     * @return
     */
    public void run() {
        try {

            openServerSocket();

            boolean processingStatus = true;
            while (processingStatus) {
                selector.select();

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();

                    if (!key.isValid()) continue;
                    if (key.isAcceptable()){
                        connectToClient(key);
                    }

                    if (key.isReadable()) {
                        if (clientSocket.isConnected()){
                            processClientRequest(key);

                        }

                    }

                }

                iterator.remove();

            }


            stop();
        } catch (OpeningServerSocketException exception) {
            logger.debug("Server cannot be started");
        } catch (ClosedChannelException e) {
            logger.debug(e.getMessage());
        } catch (ConnectionErrorException | IOException e) {
            logger.debug("Mistake with connecting occurred");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finishes server operation.
     */
    private void stop() {
        try {
            if (serverSocket == null) throw new ClosingSocketException();
            serverSocket.close();
            logger.debug("Work of server is ended");
        } catch (ClosingSocketException exception) {
            logger.debug("Cannot end the work of not yet launched server");
        } catch (IOException exception) {
            logger.debug("Mistake occurred while trying to end the work of server");
        }
    }

    /**
     * Open server socket.
     */
    private void openServerSocket() throws OpeningServerSocketException {
        try {
            logger.debug("Starting the server");

            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);

            serverSocket = serverSocketChannel.socket();
            InetSocketAddress address = new InetSocketAddress(port);
            serverSocket.bind(address);


            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            logger.debug("Server is launched successfully");

            if(serverSocket.isClosed()){

                logger.debug("Client " + clientSocket.getRemoteAddress() + " is closed");
            }

        } catch (IllegalArgumentException exception) {
            Console.printerror("Port '" + port + "' is beyond the limits of possible values!");
            throw new OpeningServerSocketException();
        } catch (IOException exception) {
            Console.printerror("Mistake occurred while trying to use the port '" + port + "'!");
            throw new OpeningServerSocketException();
        }
    }

    /**
     * Connecting to client.
     *
     * @return
     */
    private SocketChannel connectToClient(SelectionKey key) throws ConnectionErrorException, SocketTimeoutException {
        try {
            logger.debug("Listening to the port '" + port + "'");

            ServerSocketChannel serverSocketChannels = (ServerSocketChannel) key.channel();
            clientSocket = serverSocketChannels.accept();
            logger.info("socket connection accepted:" + clientSocket.socket().getRemoteSocketAddress().toString());

            clientSocket.configureBlocking(false);
            logger.debug("Connection with client is set");

            clientSocket.register(selector, SelectionKey.OP_READ);
            return clientSocket;
        } catch(SocketTimeoutException exception){
            logger.debug("Connection timeout exceeded");
            throw new SocketTimeoutException();
        } catch(IOException exception){
            logger.debug("Mistake occurred while trying to connect to client");
            throw new ConnectionErrorException();
        }
    }


    /**
     * The process of receiving a request from a client.
     */
    private void processClientRequest(SelectionKey selectionKey) throws IOException, ClassNotFoundException {
        clientSocket = (SocketChannel) selectionKey.channel();
        ByteBuffer buffer = ByteBuffer.allocate(8192);

        ClientRequest userRequest = null;
        ServerResponse responseToUser = null;
        try {
                logger.debug("Receiving client's request...");
                clientSocket.read(buffer);

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.array());
                ObjectInputStream clientReader = new ObjectInputStream(byteArrayInputStream);

                userRequest = (ClientRequest) clientReader.readObject();
                responseToUser = requester.executeCommand("", "", userRequest);
                //responseToUser = requester.createResponse(userRequest);
                logger.debug("Request '" + userRequest.getCommandName() + "' proceeded successfully.");


                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream clientWriter = new ObjectOutputStream(byteArrayOutputStream);

                byte[] responseData = byteArrayOutputStream.toByteArray();

                clientWriter.writeObject(responseToUser);
                logger.debug("Response is written");

                buffer.clear();
                buffer.put(responseData);
                buffer.flip();


                clientSocket.write(buffer);
                byteArrayOutputStream.close();
                logger.debug("Response is sent to client");

        } catch (IOException e) {
            logger.debug("Client " + clientSocket.getRemoteAddress() + " is disconnected");

            clientSocket.close();
        } catch (ClassNotFoundException e) {
            logger.debug("Client " + clientSocket.getRemoteAddress() + " is disconnected");

        }
    }

}
