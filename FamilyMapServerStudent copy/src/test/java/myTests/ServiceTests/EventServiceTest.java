package myTests.ServiceTests;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.EventDao;
import model.AuthToken;
import model.Event;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.EventRequest;
import result.EventResult;
import service.EventService;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class EventServiceTest {
    private final Event testEvent = new Event("Sheila_Birth", "sheila", "Sheila_Parker",
                                              (float) -36.1833, (float) 144.9667, "Australia","Melbourne","Sheila_Birth",1970);
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
            EventDao eventDao = new EventDao(conn);
            eventDao.insert(testEvent);
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
    public void getEventPass() {
        EventRequest eventRequest = new EventRequest(testEvent.getEventID(), testAuthToken.getAuthtoken());
        EventService eventService = new EventService();
        EventResult eventResult = eventService.getEvent(eventRequest);

        assertTrue(eventResult.isSuccess());
        Event compareEvent = new Event(eventResult.getEventID(), eventResult.getAssociatedUsername(), eventResult.getPersonID(),
                eventResult.getLatitude(), eventResult.getLongitude(), eventResult.getCountry(), eventResult.getCity(),
                eventResult.getEventType(), eventResult.getYear());
        assertEquals(testEvent, compareEvent);
    }

    @Test
    public void getEventFail() {
        EventRequest eventRequest = new EventRequest(testEvent.getEventID(), "not_a_good_authtoken");
        EventService eventService = new EventService();
        EventResult eventResult = eventService.getEvent(eventRequest);

        assertFalse(eventResult.isSuccess());
        assertNotNull(eventResult.getMessage(), "Error: Invalid auth token");
    }
}
