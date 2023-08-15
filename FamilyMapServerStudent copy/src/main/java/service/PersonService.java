package service;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.PersonDao;
import model.Person;
import request.PersonRequest;
import result.PersonResult;

import java.sql.Connection;

/**
 * PersonService class. Passes PersonRequest info into data access classes and returns PersonResult.
 */
public class PersonService {

    /**
     * Called to retrieve a person from the database.
     *
     * @param p the PersonRequest.
     * @return the PersonResult, either error response or success response.
     *
     */
    public PersonResult getPerson(PersonRequest p) {
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
                return new PersonResult("Error: Invalid auth token");
            }

            //begin finding the person
            PersonDao personDao = new PersonDao(conn);
            Person person;
            person = personDao.find(p.getPersonID());
            if (person == null) { //if there is no person with that eventID
                database.closeConnection(false);
                return new PersonResult("Error: Invalid personID parameter");
            }
            if (!person.getAssociatedUsername().equals(username)) { //if requested event doesn't belong to the user
                database.closeConnection(false);
                return new PersonResult("Error: Requested person does not belong to this user");
            }

            database.closeConnection(false);
            return new PersonResult(person.getAssociatedUsername(), person.getPersonID(),
                    person.getFirstName(), person.getLastName(), person.getGender(),
                    person.getFatherID(), person.getMotherID(), person.getSpouseID());
        } catch (DataAccessException ex) {
            try {
                database.closeConnection(false);
            } catch (DataAccessException exc) {
                exc.printStackTrace();
            }
            return new PersonResult(ex.getMessage());
        }
    }
}
