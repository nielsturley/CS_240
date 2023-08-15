package DAOJunit5Example.src.dao;

public class DataAccessException extends Exception {
    DataAccessException(String message)
    {
        super(message);
    }

    DataAccessException()
    {
        super();
    }
}
