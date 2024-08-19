package ch.zhaw.pm2.multichat.protocol;
import java.io.Serializable;
import static ch.zhaw.pm2.multichat.protocol.ConnectionHandler.DataType;

/**
 * This class models a Dataframe which is sent over the NetworkHandler.
 */
public class DataFrame implements Serializable {
    private final String sender;
    private final String receiver;
    private final DataType type;
    private final String payload;

    /**
     * Constructor
     *
     * @param sender that sent the message
     * @param receiver that will receive the message
     * @param type of the message
     * @param payload of the message
     */
    public DataFrame(String sender, String receiver, DataType type, String payload) {
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.payload = payload;
    }

    /**
     * Returns the payload of the message
     * @return the payload
     */
    public String getPayload() {
        return payload;
    }

    /**
     * Retruns the data type of the message
     * @return the data type
     */
    public DataType getType() {
        return type;
    }

    /**
     * Returns the username of the receiver
     * @return the receiver
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Returns the username of the sender
     * @return the sender
     */
    public String getSender() {
        return sender;
    }
}
