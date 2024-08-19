package ch.zhaw.pm2.checkit.server;

/**
 * Exception indicating that a record could not be found in a datasource.
 * The reason why is given as a text message.
 */
public class RecordNotFoundException extends Exception {
    public RecordNotFoundException() {
    }

    public RecordNotFoundException(String message) {
        super(message);
    }

    public RecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordNotFoundException(Throwable cause) {
        super(cause);
    }
}
