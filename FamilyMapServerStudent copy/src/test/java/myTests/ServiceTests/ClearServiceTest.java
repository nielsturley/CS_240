package myTests.ServiceTests;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import dao.*;
import model.Event;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import result.ClearResult;
import service.ClearService;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    private Database db;
    private UserDao userDao;
    private PersonDao personDao;
    private EventDao eventDao;
    private Data data;

    private static class Data {
        List<User> users;
        List<Person> persons;
        List<Event> events;

        public Data(List<User> users, List<Person> persons, List<Event> events) {
            this.users = users;
            this.persons = persons;
            this.events = events;
        }
    }


    @BeforeEach
    public void setUp() {
        try {
            db = new Database();
            Connection conn = db.getConnection();
            db.clearTables();
            userDao = new UserDao(conn);
            personDao = new PersonDao(conn);
            eventDao = new EventDao(conn);

            //parse in test data
            Gson gson = new Gson();
            try {
                JsonReader jsonReader = new JsonReader(new FileReader("src/test/java/TestData.json"));
                this.data = gson.fromJson(jsonReader, Data.class);
            } catch (FileNotFoundException e) {
                fail("cannot find TestData.json file");
            }
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
    public void clearPass() {
        try {
            //insert all data into database and close connection
            for (Event e : data.events) {
                eventDao.insert(e);
            }
            for (Person p : data.persons) {
                personDao.insert(p);
            }
            for (User u : data.users) {
                userDao.insert(u);
            }
            db.closeConnection(true);

            ClearService clearService = new ClearService();
            ClearResult clearResult = clearService.clear();

            assertTrue(clearResult.isSuccess());

            //check to make sure database is clear
            Connection conn = db.openConnection();
            EventDao eventDao1 = new EventDao(conn);
            PersonDao personDao1 = new PersonDao(conn);
            UserDao userDao1 = new UserDao(conn);

            for (Event e : data.events) {
                assertNull(eventDao1.find(e.getEventID()));
            }
            for (Person p : data.persons) {
                assertNull(personDao1.find(p.getPersonID()));
            }
            for (User u : data.users) {
                assertNull(userDao1.findFromUsername(u.getUsername()));
            }
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void clearFail() {
        try {
            db.closeConnection(true);

            //clear the database even though it is empty. shouldn't throw any errors.
            ClearService clearService = new ClearService();
            ClearResult clearResult = clearService.clear();
            assertTrue(clearResult.isSuccess());

            //check to make sure database is clear
            Connection conn = db.openConnection();
            EventDao eventDao1 = new EventDao(conn);
            PersonDao personDao1 = new PersonDao(conn);
            UserDao userDao1 = new UserDao(conn);

            for (Event e : data.events) {
                assertNull(eventDao1.find(e.getEventID()));
            }
            for (Person p : data.persons) {
                assertNull(personDao1.find(p.getPersonID()));
            }
            for (User u : data.users) {
                assertNull(userDao1.findFromUsername(u.getUsername()));
            }
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }
}
