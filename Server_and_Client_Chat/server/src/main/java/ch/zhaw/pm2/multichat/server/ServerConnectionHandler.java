package ch.zhaw.pm2.multichat.server;

import ch.zhaw.pm2.multichat.protocol.ChatProtocolException;
import ch.zhaw.pm2.multichat.protocol.ConnectionHandler;
import ch.zhaw.pm2.multichat.protocol.DataFrame;
import ch.zhaw.pm2.multichat.protocol.NetworkHandler;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.zhaw.pm2.multichat.protocol.ConnectionHandler.DataType.*;
import static ch.zhaw.pm2.multichat.protocol.ConnectionHandler.State.*;

/**
 * This is the implementation of the connection handler on the
 * server side. For each client there is one ServerConnectionHandler
 */
public class ServerConnectionHandler extends ConnectionHandler {
    private static final AtomicInteger connectionCounter = new AtomicInteger(0);
    private final int connectionId = connectionCounter.incrementAndGet();
    private final Map<String,ServerConnectionHandler> connectionRegistry;

    /**
     * Constructor
     *
     * @param connection to be used by the handler
     * @param registry to be used to store all connections
     */
    public ServerConnectionHandler(NetworkHandler.NetworkConnection<DataFrame> connection,
                                   Map<String,ServerConnectionHandler> registry) {
        super(connection);
        Objects.requireNonNull(connection, "Connection must not be null");
        Objects.requireNonNull(registry, "Registry must not be null");
        this.userName = "Anonymous-" + connectionId;
        this.connectionRegistry = registry;
    }

    /**
     * Prints unknown data type error
     *
     * @param type the message type
     */
    @Override
    protected void getDefaultMessage(DataType type) {
        System.out.println("Unknown data type received: " + type);
    }

    /**
     * Prints the error message received from the sender
     *
     * @param sender that sent the message
     * @param payload of the message
     */
    @Override
    protected void getErrorMessage(String sender, String payload) {
        System.err.println("Received error from client (" + sender + "): " + payload);
    }

    /**
     * If a disconnect request is coming from client, the client is removed
     * from the registry and the client is given a confirmation
     *
     * @param message for the disconnection
     * @throws ChatProtocolException
     */
    @Override
    protected void getDisconnectMessage(String message) throws ChatProtocolException {
        if (state.equals(DISCONNECTED))
            throw new ChatProtocolException("Illegal state for disconnect request: " + state);
        if (state.equals(CONNECTED)) {
            connectionRegistry.remove(this.userName);
        }
        sendData(USER_NONE, userName, CONFIRM, "Confirm disconnect of " + userName);
        this.state = DISCONNECTED;
        this.stopReceiving();
    }

    /**
     * Sends the message to the receiver stored in the registry.
     * If receiver is USER_ALL then it iterates over the registry
     * and sends each client the message.
     *
     * @param sender that sent the message
     * @param receiver that will receive the message
     * @param type of the message
     * @param payload of the message
     * @throws ChatProtocolException
     */
    @Override
    protected void getMessage(String sender, String receiver, DataType type, String payload) throws ChatProtocolException {
        sender = userName;
        if (state != CONNECTED) throw new ChatProtocolException("Illegal state for message request: " + state);
        if (USER_ALL.equals(receiver)) {
            for (ServerConnectionHandler handler : connectionRegistry.values()) {

                handler.sendData(sender, receiver, type, payload);
            }
        } else {
            ServerConnectionHandler handler = connectionRegistry.get(receiver);
            if (handler != null) {
                handler.sendData(sender, receiver, type, payload);
                this.sendData(sender, receiver, type, payload);
            } else {
                this.sendData(USER_NONE, userName, ERROR, "Unknown User: " + receiver);
            }
        }
    }

    /**
     * Server is not expecting confirmations.
     * Prints to terminal if any are coming in.
     *
     * @param message for the confirmation
     */
    @Override
    protected void getConfirmMessage(String message) {
        System.out.println("Not expecting to receive a CONFIRM request from client");
    }

    /**
     * If a connection request is coming from client
     * the server is checking if username is still available and
     * if so is registering it into the registry and confirms registration to client
     *
     * @param sender the message type
     * @throws ChatProtocolException if something is wrong with the message
     */
    @Override
    protected void getConnectMessage(String sender) throws ChatProtocolException {
        if (this.state != NEW) throw new ChatProtocolException("Illegal state for connect request: " + state);
        if (sender == null || sender.isBlank()) sender = this.userName;
        if (connectionRegistry.containsKey(sender)) {
            throw new ChatProtocolException("User name already taken: " + sender);
        }
        this.userName = sender;
        connectionRegistry.put(userName, this);
        sendData(USER_NONE, userName, CONFIRM, "Registration successful for " + userName);
        this.state = CONNECTED;
    }
}
