package service;

import dao.*;
import model.Event;
import model.Person;
import model.User;
import request.LoadRequest;
import result.LoadResult;

import java.sql.Connection;

/**
 * LoadService class. Passes LoadRequest info into data access classes and returns LoadResult.
 */
public class LoadService {

    /**
     * Called to load new database information. Clears the database and loads new information from the LoadRequest.
     * @param l the LoadRequest.
     * @return the LoadResult, either error response or success response.
     */
    public LoadResult load(LoadRequest l) {
        Database database = new Database();
        Connection conn;
        try {
            conn = database.openConnection();
            database.clearTables();

            //insert users
            UserDao userDao = new UserDao(conn);
            if (l.getUsers() != null) {
                for (User u : l.getUsers()) {
                    userDao.insert(u);
                }
            }

            //insert people
            PersonDao personDao = new PersonDao(conn);
            if (l.getPersons() != null) {
                for (Person p : l.getPersons()) {
                    personDao.insert(p);
                }
            }

            //insert events
            EventDao eventDao = new EventDao(conn);
            if (l.getEvents() != null) {
                for (Event e : l.getEvents()) {
                    eventDao.insert(e);
                }
            }

            database.closeConnection(true);
            return new LoadResult("Successfully added " + l.getUsers().size() + " users, " + l.getPersons().size() + " persons, and " + l.getEvents().size() + " events to the database.", true);
        } catch (DataAccessException e) {
            try {
                database.closeConnection(false);
            } catch (DataAccessException exc) {
                exc.printStackTrace();
            }
            return new LoadResult(e.getMessage(), false);
        }
    }
}
