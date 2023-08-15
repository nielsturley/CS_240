package myTests.DAOTests;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import dao.DataAccessException;
import dao.Database;
import dao.PersonDao;
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

public class PersonDAOTest {
    private Database db;
    private Person testPerson;
    private PersonDao pDao;
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
    public void setUp() {
        try {
            db = new Database();
            testPerson = new Person("00000", "sexybeast1", "Dude", "Duder", "m", "00001", "00002", "00003");
            testUser = new User("sheila", "parker", "sheila@parker.com", "Sheila",
                    "Parker", "f", "Sheila_Parker");
            Connection conn = db.getConnection();
            db.clearTables();
            pDao = new PersonDao(conn);
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
            pDao.insert(testPerson);
            Person compareTest = pDao.find(testPerson.getPersonID());
            assertNotNull(compareTest);
            assertEquals(testPerson, compareTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void insertFail() {
        try {
            pDao.insert(testPerson);
            assertThrows(DataAccessException.class, ()-> pDao.insert(testPerson));
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findPass() {
        try {
            pDao.insert(testPerson);
            Person retrieveTest = pDao.find(testPerson.getPersonID());
            assertNotNull(retrieveTest);
            assertEquals(testPerson, retrieveTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findFail() {
        try {
            Person nullTest = pDao.find(testPerson.getPersonID());
            assertNull(nullTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findForUserPass() {
        try {
            List<Person> comparePeople = new ArrayList<>();
            for (Person p : data.persons) {
                pDao.insert(p);
                if (p.getPersonID().equals(testUser.getPersonID())) {
                    comparePeople.add(p);
                }
            }
            List<Person> testPeople = pDao.findForUser(testUser.getUsername());
            assertNotNull(testPeople);

            //remove all people that are the same
            testPeople.retainAll(comparePeople);
            comparePeople.removeAll(testPeople);
            assertEquals(comparePeople.size(), 0);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findForUserFail() {
        try {
            List<Person> testPeople = pDao.findForUser(testUser.getUsername());
            assertNull(testPeople);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void clearForUserPass() {
        try {
            for (Person p : data.persons) {
                pDao.insert(p);
            }
            pDao.clearForUser(testUser.getUsername());
            List<Person> testPeople = pDao.findForUser(testUser.getUsername());
            assertNull(testPeople);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void clearForUserFail() {
        try {
            pDao.clearForUser(testUser.getUsername());
            List<Person> testPeople = pDao.findForUser(testUser.getUsername());
            assertNull(testPeople);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void clearAllPass() {
        try {
            pDao.insert(testPerson);
            pDao.clearAll();
            Person nullTest = pDao.find(testPerson.getPersonID());
            assertNull(nullTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void clearAllFail() {
        try {
            pDao.clearAll();
            Person nullTest = pDao.find(testPerson.getPersonID());
            assertNull(nullTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }
}

