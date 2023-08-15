package service;

import dao.*;
import result.ClearResult;

import java.sql.Connection;

/**
 * ClearService class. Clears the database and returns ClearResult.
 */
public class ClearService {

    /**
     * Called to clear all data from the database.
     *
     * @return the ClearResult.
     */
    public ClearResult clear() {
        Database database = new Database();
        Connection conn;
        try {
            conn = database.openConnection();

            AuthTokenDao authTokenDao = new AuthTokenDao(conn);
            EventDao eventDao = new EventDao(conn);
            PersonDao personDao = new PersonDao(conn);
            UserDao userDao = new UserDao(conn);
            authTokenDao.clearAll();
            eventDao.clearAll();
            personDao.clearAll();
            userDao.clearAll();

            database.closeConnection(true);
            return new ClearResult("Clear succeeded.", true);
        } catch (DataAccessException e) {
            try {
                database.closeConnection(false);
            } catch (DataAccessException exc) {
                exc.printStackTrace();
            }
            return new ClearResult(e.getMessage(), false);
        }
    }
}
