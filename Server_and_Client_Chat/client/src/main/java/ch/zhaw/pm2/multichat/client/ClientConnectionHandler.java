package ch.zhaw.pm2.multichat.client;

import ch.zhaw.pm2.multichat.protocol.ChatProtocolException;
import ch.zhaw.pm2.multichat.protocol.ConnectionHandler;
import ch.zhaw.pm2.multichat.protocol.NetworkHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;


import java.io.IOException;

import static ch.zhaw.pm2.multichat.protocol.ConnectionHandler.State.*;
import static ch.zhaw.pm2.multichat.protocol.ConnectionHandler.DataType.*;

/**
 * This class is responsible for the client side connection stuff of the application.
 * It's registering itself to the server and listens for incoming data which is then processed.
 */
public class ClientConnectionHandler extends ConnectionHandler {
    private Messenger messenger;
    private BooleanProperty connectedProperty = new SimpleBooleanProperty(false);

    /**
     * Constructor
     * @param serverAddress Address of the server
     * @param serverPort the Port on which the server runs
     * @param userName of the user
     * @param messenger for the ui
     * @throws IOException Signals that an I/O exception of some sort has occurred
     */
    public ClientConnectionHandler(String serverAddress, int serverPort, String userName, Messenger messenger) throws IOException {
        super(NetworkHandler.openConnection(serverAddress, serverPort));
        this.userName = (userName == null || userName.isBlank())? USER_NONE : userName;
        this.messenger = messenger;
    }

    public State getState() {
        return this.state;
    }

    public BooleanProperty getConnectedProperty() {
        return connectedProperty;
    }

    /**
     * Registers the user at the server and sets the state to CONFIRM_CONNECT
     *
     * @throws ChatProtocolException if connectionState is not NEW
     */
    public void connect() throws ChatProtocolException {
        if (state != NEW) throw new ChatProtocolException("Illegal state for connect: " + state);
        this.sendData(userName, USER_NONE, CONNECT,null);
        this.setState(CONFIRM_CONNECT);
    }

    /**
     * Unregisters the user from the server and sets the state to CONFIRM_DISCONNECT
     *
     * @throws ChatProtocolException if connectionState is not NEW and not CONNECTED
     */
    public void disconnect() throws ChatProtocolException {
        if (state != NEW && state != CONNECTED) throw new ChatProtocolException("Illegal state for disconnect: " + state);
        this.sendData(userName, USER_NONE, DISCONNECT,null);
        this.setState(CONFIRM_DISCONNECT);
    }

    /**
     * This method is called when the thread dies resets the connected state and informs the user
     */
    @Override
    protected void threadDies() {
        connectedProperty.set(false);
        messenger.writeInfo("Disconnected");
    }

    /**
     * Prints a confirmation message and changes the state accordingly
     *
     * @param payload the received message
     */
    @Override
    protected void getConfirmMessage(String payload) {
        if (state == CONFIRM_CONNECT) {
            messenger.writeInfo(payload);
            System.out.println("CONFIRM: " + payload);
            this.setState(CONNECTED);
            connectedProperty.set(true);
        } else if (state == CONFIRM_DISCONNECT) {
            System.out.println("CONFIRM: " + payload);
            messenger.writeInfo(payload);
            this.setState(DISCONNECTED);
            connectedProperty.set(false);
            this.stopReceiving();
        } else {
            System.out.println("Got unexpected confirm message: " + payload);
        }
    }

    /**
     * Writes the incoming messages into the UI
     *
     * @param sender username of sender
     * @param receiver username of receiver
     * @param type connection type
     * @param payload the sent message
     */
    @Override
    protected void getMessage(String sender, String receiver, DataType type, String payload) {
        if (state != CONNECTED) {
            System.out.println("MESSAGE: Illegal state " + state + " for message: " + payload);
            return;
        }
        messenger.receiveMessage(new Message(Message.MessageType.MESSAGE, sender, receiver, payload));
        System.out.println("MESSAGE: From " + sender + " to " + receiver + ": " + payload);
    }

    /**
     * Writes Disconnect into the UI and sets the state to DISCONNECTED
     *
     * @param payload the message
     */
    @Override
    protected void getDisconnectMessage(String payload){
        if (state == DISCONNECTED) {
            System.out.println("DISCONNECT: Already in disconnected: " + payload);
            return;
        }
        messenger.writeInfo(payload);
        System.out.println("DISCONNECT: " + payload);
        this.setState(DISCONNECTED);
        connectedProperty.set(false);
    }

    /**
     * On client side no connection requests are expected.
     * It prints the error message
     *
     * @param message received error
     */
    @Override
    protected void getConnectMessage(String message){
        System.out.println("Illegal connect request from server");
    }

    /**
     * Prints the error message received from sender
     *
     * @param sender from whom the message comes
     * @param payload the received message
     */
    @Override
    protected void getErrorMessage(String sender, String payload){
        messenger.writeError(payload);
        System.out.println("ERROR: " + payload);
    }

    /**
     * Prints unknown data type error
     *
     * @param type the data type
     * @throws ChatProtocolException
     */
    @Override
    protected void getDefaultMessage(DataType type) throws ChatProtocolException {
        System.out.println("Unknown data type received: " + type);
    }
}
