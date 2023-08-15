package myTests.ServiceTests;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import dao.*;
import model.Event;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoadRequest;
import result.LoadResult;
import service.LoadService;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoadServiceTest {
    private Database db;
    private Data data;
    private User testUser;
    private Event testEvent;

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
            db.openConnection();
            db.clearTables();
            testUser = new User("sheila", "parker", "sheila@parker.com", "Sheila",
                    "Parker", "f", "Sheila_Parker");
            testEvent = new Event("Sheila_Birth", "sheila", "Sheila_Parker",
                    (float) -36.1833, (float) 144.9667, "Australia","Melbourne","Sheila_Birth",1970);
            Gson gson = new Gson();
            try {
                JsonReader jsonReader = new JsonReader(new FileReader("src/test/java/TestData.json"));
                this.data = gson.fromJson(jsonReader, Data.class);
            } catch (FileNotFoundException e) {
                Assertions.fail("cannot find TestData.json file");
            }
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
    public void loadPass() {
        LoadRequest loadRequest = new LoadRequest(data.users, data.persons, data.events);
        LoadService loadService = new LoadService();
        LoadResult loadResult = loadService.load(loadRequest);
        assertTrue(loadResult.isSuccess());

        try {
            Connection conn = db.openConnection();

            UserDao userDao = new UserDao(conn);
            User user = userDao.findFromUsername(testUser.getUsername());
            assertNotNull(user);
            assertEquals(user, testUser);

            PersonDao personDao = new PersonDao(conn);
            List<Person> personList = personDao.findForUser(testUser.getUsername());
            assertNotNull(personList);
            List<Person> comparePersons = new ArrayList<>();
            for (Person p : data.persons) {
                if (p.getAssociatedUsername().equals(testUser.getUsername())) {
                    comparePersons.add(p);
                }
            }

            //remove all people that are the same
            personList.retainAll(comparePersons);
            comparePersons.removeAll(personList);
            assertEquals(comparePersons.size(), 0); //none left over


            EventDao eventDao = new EventDao(conn);
            List<Event> eventList = eventDao.findForUser(testUser.getUsername());
            assertNotNull(eventList);
            List<Event> compareEvents = new ArrayList<>();
            for (Event e : data.events) {
                if (e.getAssociatedUsername().equals(testUser.getUsername())) {
                    compareEvents.add(e);
                }
            }
            //remove all events that are the same
            eventList.retainAll(compareEvents);
            compareEvents.removeAll(eventList);
            assertEquals(compareEvents.size(), 0); //none left over
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void loadFail() {
        List<Event> duplicateEventList = new ArrayList<>();
        duplicateEventList.add(testEvent);
        duplicateEventList.add(testEvent);
        LoadRequest loadRequest = new LoadRequest(null, null, duplicateEventList);
        LoadService loadService = new LoadService();
        LoadResult loadResult = loadService.load(loadRequest);

        assertFalse(loadResult.isSuccess());
        try {
            Connection conn = db.openConnection();
            EventDao eventDao = new EventDao(conn);
            List<Event> eventList = eventDao.findForUser(testUser.getUsername());
            assertNull(eventList);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }
}
