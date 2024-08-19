package ch.zhaw.pm2.multichat.client;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Objects;

/**
 * Holds a list of the messages and allows for adding both chat and
 * information messages. Provides bound to allow a GUI to listen to changes.
 * Filters also can be applied in order to
 * display only messages containing a certain keyword.
 */
public class ClientMessageList {
    private ObservableList<Message> messages = FXCollections.observableArrayList();
    private StringProperty messageBound = new SimpleStringProperty();

    /**
     * Returns a StringProperty which contains the messages in a string format
     *
     * @return a simple string property
     */
    public StringProperty getToBeShownMessages() {
        return messageBound;
    }

    /**
     * Constructor
     */
    ClientMessageList() {
        messages.addListener(new ListChangeListener<Message>() {
            @Override
            public void onChanged(Change<? extends Message> c) {
                rewriteMessageBound();
            }
        });
    }

    /**
     * Rewrites the string format of the messages
     */
    private void rewriteMessageBound() {
        writeFilteredMessages("");
    }

    /**
     * Adds a chat message to be displayed in the chat-window
     *
     * @param message the message itself
     */
    public void addMessage(Message message) {
        messages.add(message);
    }

    /**
     * Filters the message list to show only specific messages based on the filter choice
     *
     * @param filter the filter to use
     */
	public void writeFilteredMessages(String filter) {
		boolean showAll = filter == null || filter.isBlank();
		messageBound.set("");
        for (Message message : messages) {
            String sender = Objects.requireNonNullElse(message.getSender(), "");
            String receiver = Objects.requireNonNullElse(message.getReceiver(), "");
            String messageBody = Objects.requireNonNull(message.getMessage(), "");
            if (showAll ||
                sender.contains(filter) ||
                receiver.contains(filter) ||
                messageBody.contains(filter)) {
                switch (message.getType()) {
                    case MESSAGE -> writeMessage(sender, receiver, messageBody);
                    case ERROR -> writeError(messageBody);
                    case INFO -> writeInfo(messageBody);
                    default -> writeError("Unexpected message type: " + message.getType());
                }
            }
        }
	}

    private void appendTextMessage(String message) {
        messageBound.set(messageBound.get() + message);
    }

    /**
     * Prints [ERROR] + message to a newline in the messageArea of the UI
     *
     * @param message the error message
     */
    private void writeError(String message) {
        appendTextMessage(String.format("[ERROR] %s\n", message));
    }

    /**
     * Prints [INFO] + message to a newline in the messageArea of the UI
     *
     * @param message the info message
     */
    private void writeInfo(String message) {
        appendTextMessage(String.format("[INFO] %s\n", message));
    }

    /**
     * Prints [sender -> receiver] + message to a newline in the messageArea of the UI
     *
     * @param sender that sent the message
     * @param receiver that will receive the message
     * @param message the message
     */
    private void writeMessage(String sender, String receiver, String message) {
        appendTextMessage(String.format("[%s -> %s] %s\n", sender, receiver, message));
    }

    /**
     * Adds an info message to be displayed in the chat-window
     *
     * @param message the info message to display
     */
    public void addInfo(String message) {
        messages.add(new Message(Message.MessageType.INFO, null, null, message));
    }

    /**
     * Adds an error message to be displayed in the chat-window
     *
     * @param message the error message to display
     */
    public void addError(String message) {
        messages.add(new Message(Message.MessageType.ERROR, null, null, message));
    }
}
