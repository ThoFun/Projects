package ch.zhaw.pm2.multichat.protocol;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

import static ch.zhaw.pm2.multichat.protocol.ConnectionHandler.State.*;
import static ch.zhaw.pm2.multichat.protocol.ConnectionHandler.DataType.*;

/**
 * This class is a super class for Connection Handlers
 * and handles all the connection stuff. Methods used in more
 * specific use cases (Client / Serverside) are declared abstract.
 */
public abstract class ConnectionHandler implements Runnable {
    public static final String USER_NONE = "";
    public static final String USER_ALL = "*";
    private final NetworkHandler.NetworkConnection<DataFrame> connection;
    protected String userName = USER_NONE;
    protected State state = NEW;

    @Override
    public void run() {
        startReceiving();
    }

    /**
     * This enum represents the different data types that can be sent out.
     * CONNECT: Used by a client to request a formal connection to the server.
     * CONFIRM: Confirm a connect or disconnect request.
     * DISCONNECT: Used by a client to request a formal connection to the server or a server to inform the client about a disconnect.
     * MESSAGE: Standard message containing a text message as payload.
     * ERROR: Error message to inform the recipient about an error.
     */
    public enum DataType {
        CONNECT, CONFIRM, DISCONNECT, MESSAGE, ERROR
    }
    /**
     * This enum is representing the different connection states of the connection handler.
     * NEW: New, needs to be connected to a server
     * CONFIRM_CONNECT: Waits for confirmation of server
     * CONNECTED: Got confirmation, ready to work properly
     * CONFIRM_DISCONNECT: Sent disconnect request to the server, waiting for its confirmation.
     * DISCONNECTED: There is no Connection available
     */
    public enum State {
        NEW, CONFIRM_CONNECT, CONNECTED, CONFIRM_DISCONNECT, DISCONNECTED
    }

    /**
     * Constructor. This is the minimum constructor. Every instant of the ConnectionHandler classes
     * has to have at least a connection.
     * @param connection that should be used
     */
    protected ConnectionHandler(NetworkHandler.NetworkConnection<DataFrame> connection)  {
        this.connection = connection;
    }

    public State getConnectionState() {
        return this.state;
    }

    public void setState (State newState) {
        this.state = newState;
    }

    /**
     * Method to start the connection handler. Executed by the Java Thread class in a separate thread.
     * Starts into while loop where it is waiting for data to be processed
     * Depending on the errors which are thrown, logging them with different messages
     */
    public void startReceiving() {
        System.out.println("Starting Connection Handler");
        try {
            System.out.println("Start receiving data...");
            while (connection.isAvailable()) {
                DataFrame data = connection.receive();
                processData(data);
            }
            System.out.println("Stopped recieving data");
        } catch (SocketException e) {
            System.out.println("Connection terminated locally");
            this.setState(DISCONNECTED);
            System.err.println("Unregistered because connection terminated" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("Connection terminated by remote");
            this.setState(DISCONNECTED);
            System.err.println("Unregistered because connection terminated" + e.getMessage());
        } catch(IOException e) {
            System.err.println("Communication error" + e);
        } catch(ClassNotFoundException e) {
            System.err.println("Received object of unknown type" + e.getMessage());
        } catch (ChatProtocolException e) {
            e.printStackTrace();
        }
        System.out.println("Stopped Connection Handler");
        threadDies();
    }

    protected void threadDies(){};

     /**
     * Processes incoming data and takes different actions depending
     * on their data type.
      *
     * @param data to process
     * @throws ChatProtocolException if either sender,receiver,type are null
     */
    private void processData(DataFrame data) throws ChatProtocolException {
        try {
            // dispatch operation based on type parameter
            switch (data.getType()){
                case CONFIRM -> getConfirmMessage(data.getPayload());
                case CONNECT -> getConnectMessage(data.getSender());
                case DISCONNECT -> getDisconnectMessage(data.getPayload());
                case MESSAGE -> getMessage(data.getSender(), data.getReceiver(), data.getType(), data.getPayload());
                case ERROR -> getErrorMessage(data.getSender(), data.getPayload());
                default -> getDefaultMessage(data.getType());
            }
        } catch (ChatProtocolException e) {
            System.err.println("Error while processing data: " + e.getMessage());
            sendData(USER_NONE, userName, ERROR, e.getMessage());
        }
    }

    /**
     * Closes connection handler to server
     */
    public void stopReceiving() {
        System.out.println("Closing Connection Handler to Server");
        try {
            System.out.println("Stop receiving data...");
            connection.close();
            System.out.println("Stopped receiving data.");
        } catch (IOException e) {
            System.err.println("Failed to close connection." + e.getMessage());
        }
        System.out.println("Closed Connection Handler to Server");
    }

    /**
     * If there is a connection to the server, builds data out of parameters and sends it to the server.
     *
     * @param sender that sent the message
     * @param receiver that will receive the message
     * @param type of the message
     * @param payload of the message
     */
    public void sendData(String sender, String receiver, DataType type, String payload) {
        if (connection.isAvailable()) {
            DataFrame data = new DataFrame(sender, receiver, type, payload);
            try {
                connection.send(data);

            } catch(IOException e) {
                System.err.println("Communication error: " + e.getMessage());
            }
        }
    }

    /**
     * Sends a message and the name of the receiver to the server
     * @param receiver of the message
     * @param message to be sent
     * @throws ChatProtocolException if the state of the connectionHandler is not 'CONNECTED'
    */
    public void message(String receiver, String message) throws ChatProtocolException {
        if (state != CONNECTED) throw new ChatProtocolException("Illegal state for message: " + state);
        this.sendData(userName, receiver, MESSAGE,message);
    }

    public String getUserName(){return userName;}

    /**
     * This method defines what happens if a confirmation has been received.
     *
     * @param message for the confirmation
     * @throws ChatProtocolException if something is wrong with the message
     */
    protected abstract void getConfirmMessage(String message) throws ChatProtocolException;

    /**
     * Defines what happens if a text message has been received
     *
     * @param sender that sent the message
     * @param receiver that will receive the message
     * @param type of the message
     * @param payload of the message
     * @throws ChatProtocolException if something is wrong with the message
     */
    protected abstract void getMessage(String sender, String receiver, DataType type, String payload) throws ChatProtocolException;

    /**
     * Defines what happens if a disconnect request has been received
     *
     * @param message for the disconnection
     * @throws ChatProtocolException if something is wrong with the message
     */
    protected abstract void getDisconnectMessage(String message) throws ChatProtocolException;

    /**
     * Handles Connection requests
     *
     * @param message the connect message
     * @throws ChatProtocolException if something is wrong with the message
     */
    protected abstract void getConnectMessage(String message) throws ChatProtocolException;

    /**
     * Defines what happens if an error message has been received
     *
     * @param sender that sent the message
     * @param payload of the message
     * @throws ChatProtocolException
     */
    protected abstract void getErrorMessage(String sender, String payload) throws ChatProtocolException;

    /**
     * defines what happens if no specific message type has been received.
     *
     * @param type the message type
     * @throws ChatProtocolException if something is wrong with the message
     */
    protected abstract void getDefaultMessage(DataType type) throws ChatProtocolException;
}
