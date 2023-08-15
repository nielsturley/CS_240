package myTests.ServiceTests;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.PersonDao;
import model.AuthToken;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.PersonRequest;
import result.PersonResult;
import service.PersonService;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PersonServiceTest {
    private Person testPerson;
    private AuthToken testAuthToken;
    private Database db;


    @BeforeEach
    public void setUp() {
        try {
            db = new Database();
            Connection conn = db.getConnection();
            db.clearTables();
            User testUser = new User("sheila", "parker", "sheila@parker.com", "Sheila",
                    "Parker", "f", "Sheila_Parker");
            testPerson = new Person("Sheila_Parker", "sheila", "Sheila", "Parker",
                    "f", "Blaine_McGary", "Betty_White", "Davis_Hyer");
            PersonDao PersonDao = new PersonDao(conn);
            PersonDao.insert(testPerson);
            AuthTokenDao authTokenDao = new AuthTokenDao(conn);
            testAuthToken = authTokenDao.generateToken(testUser.getUsername());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            try {
                db.getConnection();
                db.closeConnection(false);
            } catch (DataAccessException ex) {
                ex.printStackTrace();
            }
            fail(e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        try {
            db.getConnection();
            db.closeConnection(false);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getPersonPass() {
        PersonRequest personRequest = new PersonRequest(testPerson.getPersonID(), testAuthToken.getAuthtoken());
        PersonService personService = new PersonService();
        PersonResult personResult = personService.getPerson(personRequest);

        assertTrue(personResult.isSuccess());
        Person comparePerson = new Person(personRequest.getPersonID(), personResult.getAssociatedUsername(), personResult.getFirstName(),
                personResult.getLastName(), personResult.getGender(), personResult.getFatherID(), personResult.getMotherID(),
                personResult.getSpouseID());
        assertEquals(testPerson, comparePerson);
    }

    @Test
    public void getPersonFail() {
        PersonRequest PersonRequest = new PersonRequest(testPerson.getPersonID(), "not_a_good_authtoken");
        PersonService PersonService = new PersonService();
        PersonResult PersonResult = PersonService.getPerson(PersonRequest);

        assertFalse(PersonResult.isSuccess());
        assertEquals(PersonResult.getMessage(), "Error: Invalid auth token");
    }
}
