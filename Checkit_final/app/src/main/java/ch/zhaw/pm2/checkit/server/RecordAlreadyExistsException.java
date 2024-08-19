package ch.zhaw.pm2.checkit.server;

/**
 * Exception indicating that a record exists, which shouldn't exist based on the operation
 */
public class RecordAlreadyExistsException extends Exception {
    public RecordAlreadyExistsException() {
    }

    public RecordAlreadyExistsException(String message) {
        super(message);
    }

    public RecordAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
