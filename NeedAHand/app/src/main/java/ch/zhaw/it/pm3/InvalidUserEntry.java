package ch.zhaw.it.pm3;

/**
 * class used to handle invalid user entries.
 */
public class InvalidUserEntry extends Exception{

    public InvalidUserEntry(String errorMessage) {
        super(errorMessage);
    }
}
