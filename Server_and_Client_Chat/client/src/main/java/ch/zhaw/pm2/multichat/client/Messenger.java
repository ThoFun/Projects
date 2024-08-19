package ch.zhaw.pm2.multichat.client;

import ch.zhaw.pm2.multichat.protocol.ChatProtocolException;
import ch.zhaw.pm2.multichat.protocol.ConnectionHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;

import static ch.zhaw.pm2.multichat.protocol.ConnectionHandler.State.CONNECTED;

/**
 * Class processes the messages for the client. It represents the model of the client
 */
public class Messenger {
    private ClientConnectionHandler connectionHandler;
    private ClientMessageList messageList = new ClientMessageList();
    private String userName;

    public StringProperty getMessageBound() {
        return messageList.getToBeShownMessages();
    }

    public BooleanProperty getConnectedProperty() {
        return connectionHandler.getConnectedProperty();}

    /**
     * Either connects or disconnects, based on the current connection state
     *
     * @param userName the username of the sender
     * @param serverAddress the server address
     * @param serverPort the port that the server uses
     */
    public void toggleConnection(String userName, String serverAddress, String serverPort) {
        if (connectionHandler == null || connectionHandler.getState() != CONNECTED) {
            connect(userName, serverAddress, serverPort);
        } else {
            disconnect();
        }
    }

    /**
     * Connects the client to a Server
     *
     * @param userName the username of the sender
     * @param serverAddress the server address
     * @param serverPort the port that the server uses
     */
    private void connect(String userName, String serverAddress, String serverPort) {
        try {
            startConnectionHandler(userName, serverAddress, Integer.parseInt(serverPort));
            connectionHandler.connect();
        } catch(Exception e) {
            messageList.addError(e.getMessage());
        }
    }

    /**
     * Disconnects client from server if there is a connection, otherwise adds Error to MessageList
     */
    public void disconnect() {
        if (connectionHandler == null) {
            messageList.addError("No connection handler");
            return;
        }
        try {
            connectionHandler.disconnect();
        } catch (ChatProtocolException e) {
            messageList.addError(e.getMessage());
        }
    }

    /**
     * Starts the connectionHandler based on the parameters.
     *
     * @param userName the username of the sender
     * @param serverAddress the server address
     * @param serverPort the port that the server uses
     * @throws IOException if an error occurred opening the connection, e.g. server is not responding.
     */
    private void startConnectionHandler(String userName, String serverAddress, int serverPort) throws IOException {
        this.userName = userName;
        connectionHandler = new ClientConnectionHandler(serverAddress, serverPort, this.userName, this);
        new Thread(connectionHandler).start();
    }

    /**
     * Adds a info message to the messageList
     *
     * @param text the info message
     */
    public void writeInfo(String text) {
        messageList.addInfo(text);
    }

    /**
     * Adds a error message to the messageList
     *
     * @param text the erro message
     */
    public void writeError(String text) {
        messageList.addError(text);
    }

    /**
     * Sends the message via connectionHandler
     *
     * @param messageType the type of the message
     * @param receiver that will receive the message
     * @param messageBody the message itself
     */
    public void sendMessage(Message.MessageType messageType, String receiver, String messageBody) {
        if (connectionHandler == null) {
            messageList.addError("No connection handler");
            return;
        }
        connectionHandler.sendData(userName, receiver, ConnectionHandler.DataType.MESSAGE, messageBody);
    }

    /**
     * Sets a filter for the client
     *
     * @param filter the search param
     */
    public void setFilter(String filter) {
        messageList.writeFilteredMessages(filter);
    }

    /**
     * Processes received messages
     *
     * @param message the message itself
     */
    public void receiveMessage(Message message) {
        messageList.addMessage(message);
    }
}
