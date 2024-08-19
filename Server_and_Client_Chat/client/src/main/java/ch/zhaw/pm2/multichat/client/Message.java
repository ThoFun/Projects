package ch.zhaw.pm2.multichat.client;

/**
 * This class models a Message
 */
public class Message {
    private MessageType type;
    private String sender;
    private String receiver;
    private String message;

    /**
     * This Enum represents the different message types.
     * INFO: An informational message for the recipient
     * MESSAGE: Standard message containing a text message as payload.
     * ERROR: Error message to inform the recipient about an error.
     */
    public enum MessageType {
        INFO, MESSAGE, ERROR;
    }

    /**
     * Constructor
     * @param sender that sent the message
     * @param receiver that will receive the message
     * @param messageType of the message
     * @param message the message
     */
    Message(MessageType messageType, String sender, String receiver, String message ) {
        this.type = messageType;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public MessageType getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }
}
