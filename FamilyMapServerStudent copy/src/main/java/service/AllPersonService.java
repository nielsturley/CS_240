package service;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.PersonDao;
import model.Person;
import request.AllPersonRequest;
import result.AllPersonResult;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * AllPersonService class. Passes AllPersonRequest info into data access classes and returns AllPersonResult.
 */
public class AllPersonService {

    /**
     * Called to retrieve all people associated with a user from the database.
     *
     * @param p the AllPersonRequest.
     * @return the AllPersonResult, either error response or success response.
     */
    public AllPersonResult getAllPeople(AllPersonRequest p) {
        String token = p.getAuthtoken();
        Database database = new Database();
        Connection conn;
        try {
            conn = database.openConnection();

            //check authtoken database for the provided authtoken
            AuthTokenDao authDao = new AuthTokenDao(conn);
            String username;
            username = authDao.findUser(token);
            if (username == null) { //if there is no user for this authtoken
                database.closeConnection(false);
                return new AllPersonResult("Error: Invalid auth token");
            }

            //begin finding all people associated with user
            PersonDao pDao = new PersonDao(conn);
            List<Person> people;
            people = new ArrayList<>(pDao.findForUser(username));

            database.closeConnection(false);
            return new AllPersonResult(people);
        } catch (DataAccessException ex) {
            try {
                database.closeConnection(false);
            } catch (DataAccessException exc) {
                exc.printStackTrace();
            }
            return new AllPersonResult(ex.getMessage());
        }
    }
}
