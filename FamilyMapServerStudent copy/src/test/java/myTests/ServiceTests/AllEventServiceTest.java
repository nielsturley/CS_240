package myTests.ServiceTests;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.EventDao;
import model.AuthToken;
import model.Event;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.AllEventRequest;
import result.AllEventResult;
import service.AllEventService;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AllEventServiceTest {
    private final User testUser = new User("sheila", "parker", "sheila@parker.com", "Sheila",
            "Parker", "f", "Sheila_Parker");
    private AuthToken testAuthToken;
    private Data data;
    private Database db;

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
            EventDao eventDao = new EventDao(conn);
            AuthTokenDao authTokenDao = new AuthTokenDao(conn);
            testAuthToken = authTokenDao.generateToken(testUser.getUsername());

            //parse in test data
            Gson gson = new Gson();
            try {
                JsonReader jsonReader = new JsonReader(new FileReader("src/test/java/TestData.json"));
                this.data = gson.fromJson(jsonReader, Data.class);
            } catch (FileNotFoundException e) {
                Assertions.fail("cannot find TestData.json file");
            }
            for (Event e : data.events) {
                eventDao.insert(e);
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
    public void getAllEventsPass() {
        AllEventRequest allEventRequest = new AllEventRequest(testAuthToken.getAuthtoken());
        AllEventService allEventService = new AllEventService();
        AllEventResult allEventResult = allEventService.getAllEvents(allEventRequest);

        assertTrue(allEventResult.isSuccess());
        List<Event> testEvents = allEventResult.getData();
        assertNotNull(testEvents);

        //find all the events that should have been retrieved from the database
        List<Event> compareEvents = new ArrayList<>();
        for (Event e : data.events) {
            if (e.getPersonID().equals(testUser.getPersonID())) {
                compareEvents.add(e);
            }
        }

        //remove all events that are the same, aka check that testEvents = compareEvents
        testEvents.retainAll(compareEvents);
        compareEvents.removeAll(testEvents);
        assertEquals(compareEvents.size(), 0);
    }

    @Test
    public void getAllEventsFail() {
        //should send a bad request since the authtoken is not in the database
        AllEventRequest allEventRequest = new AllEventRequest("not_a_good_authtoken");
        AllEventService allEventService = new AllEventService();
        AllEventResult allEventResult = allEventService.getAllEvents(allEventRequest);

        assertFalse(allEventResult.isSuccess());
        assertEquals(allEventResult.getMessage(), "Error: Invalid auth token");
    }

}
