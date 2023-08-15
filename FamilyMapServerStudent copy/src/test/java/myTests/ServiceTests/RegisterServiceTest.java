package myTests.ServiceTests;

import dao.*;
import model.Event;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import result.RegisterResult;
import service.RegisterService;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTest {
    private Database db;
    private User unregisteredTestUser;
    private static final int PEOPLE_CREATED_ON_REGISTRATION = 31;
    private static final int EVENTS_CREATED_ON_REGISTRATION = 91;


    @BeforeEach
    public void setUp() {
        db = new Database();
        try {
            db.getConnection();
            db.clearTables();
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
        unregisteredTestUser = new User("sexybeast1", "1234567", "bob@thebuilder.com", "Dude",
                    "Duder", "m", null);

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
    public void registerSuccess() {
        RegisterRequest registerRequest = new RegisterRequest(unregisteredTestUser.getUsername(), unregisteredTestUser.getPassword(),
                unregisteredTestUser.getEmail(), unregisteredTestUser.getFirstName(), unregisteredTestUser.getLastName(),
                unregisteredTestUser.getGender());
        RegisterService registerService = new RegisterService();
        RegisterResult registerResult = registerService.register(registerRequest);
        assertTrue(registerResult.isSuccess());
        unregisteredTestUser.setPersonID(registerResult.getPersonID());

        try {
            Connection conn = db.getConnection();

            //see if the user was actually registered into the database
            UserDao userDao = new UserDao(conn);
            User compareUser = userDao.findFromUsername(unregisteredTestUser.getUsername());
            assertEquals(unregisteredTestUser, compareUser);

            //see if the authtoken was generated correctly
            AuthTokenDao authTokenDao = new AuthTokenDao(conn);
            String username = authTokenDao.findUser(registerResult.getAuthtoken());
            assertEquals(username, unregisteredTestUser.getUsername());

            //see if the people were generated correctly
            PersonDao personDao = new PersonDao(conn);
            List<Person> personList;
            personList = personDao.findForUser(unregisteredTestUser.getUsername());
            assertNotNull(personList);
            assertEquals(personList.size(), PEOPLE_CREATED_ON_REGISTRATION);

            //see if the events were generated correctly
            EventDao eventDao = new EventDao(conn);
            List<Event> eventList = eventDao.findForUser(unregisteredTestUser.getUsername());
            assertNotNull(eventList);
            assertEquals(eventList.size(), EVENTS_CREATED_ON_REGISTRATION);

        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void registerFail() {
        unregisteredTestUser.setPersonID("a_random_but_good_personID"); //just so I can insert the person
        try {
            Connection conn = db.openConnection();
            UserDao userDao = new UserDao(conn);
            userDao.insert(unregisteredTestUser);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }

        //oops someone has the same username...
        RegisterRequest registerRequest = new RegisterRequest(unregisteredTestUser.getUsername(), unregisteredTestUser.getPassword(),
                unregisteredTestUser.getEmail(), unregisteredTestUser.getFirstName(), unregisteredTestUser.getLastName(),
                unregisteredTestUser.getGender());
        RegisterService registerService = new RegisterService();
        RegisterResult registerResult = registerService.register(registerRequest);
        assertFalse(registerResult.isSuccess());
        assertEquals(registerResult.getMessage(), "Error: Username already taken by another user");
    }

}
