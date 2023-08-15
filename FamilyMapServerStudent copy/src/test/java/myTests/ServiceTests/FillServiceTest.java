package myTests.ServiceTests;

import dao.*;
import json.Location;
import model.Event;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.FillRequest;
import result.FillResult;
import service.FillService;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FillServiceTest {
    private User testUser;
    private Database db;
    private static final int MIN_MARRIAGE_AGE = 13;
    private static final int MIN_PREGNANT_AGE = 13;
    private static final int MAX_PREGNANT_AGE = 50;
    private static final int MAX_DEATH_AGE = 120;
    private List<Event> testEvents;
    private List<Person> testPeople;


    @BeforeEach
    public void setUp() {
        try {
            db = new Database();
            Connection conn = db.openConnection();
            db.clearTables();
            testUser = new User("sheila", "parker", "sheila@parker.com", "Sheila",
                    "Parker", "f", "Sheila_Parker");
            UserDao userDao = new UserDao(conn);
            userDao.insert(testUser);
            db.closeConnection(true);
            testEvents = null;
            testPeople = null;
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
    public void fillPass() {
        try {
            FillRequest FillRequest = new FillRequest(testUser.getUsername(), 4);
            FillService FillService = new FillService();
            FillResult FillResult = FillService.fill(FillRequest);
            assertTrue(FillResult.isSuccess());

            Connection conn = db.openConnection();
            PersonDao personDao = new PersonDao(conn);
            testPeople = personDao.findForUser(testUser.getUsername());
            Person rootPerson = personDao.find(testUser.getPersonID());
            assertNotNull(testPeople);

            EventDao eventDao = new EventDao(conn);
            testEvents = eventDao.findForUser(testUser.getUsername());
            assertNotNull(testEvents);

            areEventsGood(rootPerson, 3);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void fillFail() {
        try {
            FillRequest FillRequest = new FillRequest("not_a_good_username", 4);
            FillService FillService = new FillService();
            FillResult FillResult = FillService.fill(FillRequest);
            assertFalse(FillResult.isSuccess());

            Connection conn = db.openConnection();
            PersonDao personDao = new PersonDao(conn);
            testPeople = personDao.findForUser(testUser.getUsername());
            assertNull(testPeople);

            EventDao eventDao = new EventDao(conn);
            testEvents = eventDao.findForUser(testUser.getUsername());
            assertNull(testEvents);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    private void areEventsGood(Person person, int generations) {
        Person father = null;
        Person mother = null;
        for (Person p : testPeople) {
            if (person.getMotherID().equals(p.getPersonID())) {
                mother = p;
            }
            if (person.getFatherID().equals(p.getPersonID())) {
                father = p;
            }
        }
        assertNotNull(father);
        assertNotNull(mother);

        Event personBirth = null;
        Event motherBirth = null;
        Event motherMarriage = null;
        Event motherDeath = null;
        Event fatherBirth = null;
        Event fatherMarriage = null;
        Event fatherDeath = null;
        for (Event e : testEvents) {
            if (e.getPersonID().equals(person.getPersonID()) && e.getEventType().equals("Birth")) {
                personBirth = e;
            }
            else if (e.getPersonID().equals(person.getMotherID()) && e.getEventType().equals("Birth")) {
                motherBirth = e;
            }
            else if (e.getPersonID().equals(person.getFatherID()) && e.getEventType().equals("Birth")) {
                fatherBirth = e;
            }
            else if (e.getPersonID().equals(person.getMotherID()) && e.getEventType().equals("Marriage")) {
                motherMarriage = e;
            }
            else if (e.getPersonID().equals(person.getFatherID()) && e.getEventType().equals("Marriage")) {
                fatherMarriage = e;
            }
            else if (e.getPersonID().equals(person.getMotherID()) && e.getEventType().equals("Death")) {
                motherDeath = e;
            }
            else if (e.getPersonID().equals(person.getFatherID()) && e.getEventType().equals("Death")) {
                fatherDeath = e;
            }
        }


        assertNotNull(personBirth);
        int personBirthYear = personBirth.getYear();

        assertNotNull(fatherBirth);
        int fatherBirthYear = fatherBirth.getYear();
        int fatherAgeAtPersonBirth = personBirthYear - fatherBirthYear;
        assertTrue(fatherAgeAtPersonBirth >= MIN_PREGNANT_AGE);

        assertNotNull(motherBirth);
        int motherBirthYear = motherBirth.getYear();
        int motherAgeAtPersonBirth = personBirthYear - motherBirthYear;
        assertTrue(motherAgeAtPersonBirth >= MIN_PREGNANT_AGE);
        assertTrue(motherAgeAtPersonBirth <= MAX_PREGNANT_AGE);

        assertNotNull(fatherMarriage);
        assertNotNull(motherMarriage);
        int fatherMarriageYear = fatherMarriage.getYear();
        int fatherAgeAtMarriage = fatherMarriageYear - fatherBirthYear;
        assertTrue(fatherAgeAtMarriage >= MIN_MARRIAGE_AGE);

        int motherMarriageYear = motherMarriage.getYear();
        int motherAgeAtMarriage = motherMarriageYear - motherBirthYear;
        assertTrue(motherAgeAtMarriage >= MIN_MARRIAGE_AGE);

        assertEquals(fatherMarriageYear, motherMarriageYear);

        Location marriageFatherLocation = new Location(fatherMarriage.getCountry(), fatherMarriage.getCity(),
                fatherMarriage.getLatitude(), fatherMarriage.getLongitude());
        Location marriageMotherLocation = new Location(motherMarriage.getCountry(), motherMarriage.getCity(),
                motherMarriage.getLatitude(), motherMarriage.getLongitude());
        assertEquals(marriageFatherLocation, marriageMotherLocation);

        assertNotNull(fatherDeath);
        assertNotNull(motherDeath);
        int fatherDeathYear = fatherDeath.getYear();
        assertTrue(fatherDeathYear >= personBirthYear);
        int fatherAgeAtDeath = fatherDeathYear - fatherBirthYear;
        assertTrue(fatherAgeAtDeath <= MAX_DEATH_AGE);

        int motherDeathYear = motherDeath.getYear();
        assertTrue(motherDeathYear >= personBirthYear);
        int motherAgeAtDeath = motherDeathYear - motherBirthYear;
        assertTrue(motherAgeAtDeath <= MAX_DEATH_AGE);




        if (generations > 0) {
            areEventsGood(mother, generations - 1);
            areEventsGood(father, generations - 1);
        }
    }
}
