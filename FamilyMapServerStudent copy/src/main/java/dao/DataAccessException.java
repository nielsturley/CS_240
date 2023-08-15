package dao;

/**
 * Exception thrown with database errors.
 */
public class DataAccessException extends Exception {
    /**
     * Default constructor. Calls the super constructor.
     */
    public DataAccessException() { super(); }

    /**
     * Constructor with message. Calls the super(String) constructor.
     *
     * @param message the message String.
     */
    public DataAccessException(String message) { super(message); }

}

