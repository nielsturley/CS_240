package myTests.DAOTests;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import dao.DataAccessException;
import dao.Database;
import dao.EventDao;
import model.Event;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventDAOTest {
    private Database db;
    private Event testEvent;
    private EventDao eDao;
    private Data data;
    private User testUser;

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
    public void setUp()
    {
        db = new Database();
        testEvent = new Event("Biking_123A", "Gale", "Gale123A",
                35.9f, 140.1f, "Japan", "Ushiku",
                "Biking_Around", 2016);
        testUser = new User("sheila", "parker", "sheila@parker.com", "Sheila",
                "Parker", "f", "Sheila_Parker");
        try {
            Connection conn = db.getConnection();
            db.clearTables();
            eDao = new EventDao(conn);
            Gson gson = new Gson();
            try {
                JsonReader jsonReader = new JsonReader(new FileReader("src/test/java/TestData.json"));
                this.data = gson.fromJson(jsonReader, Data.class);
            } catch (FileNotFoundException e) {
                Assertions.fail("cannot find TestData.json file");
            }
        } catch (DataAccessException e) {
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
    public void insertPass() {
        try {
            eDao.insert(testEvent);
            Event compareTest = eDao.find(testEvent.getEventID());
            assertNotNull(compareTest);
            assertEquals(testEvent, compareTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void insertFail() {
        try {
            eDao.insert(testEvent);
            assertThrows(DataAccessException.class, ()-> eDao.insert(testEvent));
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findPass() {
        try {
            eDao.insert(testEvent);
            Event retrieveTest = eDao.find(testEvent.getEventID());
            assertNotNull(retrieveTest);
            assertEquals(testEvent, retrieveTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findFail() {
        try {
            Event nullTest = eDao.find(testEvent.getEventID());
            assertNull(nullTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findForUserPass() {
        try {
            List<Event> compareEvents = new ArrayList<>();
            for (Event e : data.events) {
                eDao.insert(e);
                if (e.getPersonID().equals(testUser.getPersonID())) {
                    compareEvents.add(e);
                }
            }
            List<Event> testEvents = eDao.findForUser(testUser.getUsername());
            assertNotNull(testEvents);

            //remove all events that are the same
            testEvents.retainAll(compareEvents);
            compareEvents.removeAll(testEvents);
            assertEquals(compareEvents.size(), 0); //none left over
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void findForUserFail() {
        try {
            List<Event> testEvents = eDao.findForUser(testUser.getUsername());
            assertNull(testEvents);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void clearForUserPass() {
        try {
            for (Event e : data.events) {
                eDao.insert(e);
            }
            eDao.clearForUser(testUser.getUsername());
            List<Event> testEvents = eDao.findForUser(testUser.getUsername());
            assertNull(testEvents);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void clearForUserFail() {
        try {
            eDao.clearForUser(testUser.getUsername());
            List<Event> testEvents = eDao.findForUser(testUser.getUsername());
            assertNull(testEvents);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void clearAllPass() {
        try {
            eDao.insert(testEvent);
            eDao.clearAll();
            Event nullTest = eDao.find(testEvent.getEventID());
            assertNull(nullTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void clearAllFail() {
        try {
            eDao.clearAll();
            Event nullTest = eDao.find(testEvent.getEventID());
            assertNull(nullTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }
}
