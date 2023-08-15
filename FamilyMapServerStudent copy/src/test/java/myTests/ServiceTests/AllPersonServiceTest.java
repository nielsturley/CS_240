package myTests.ServiceTests;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.PersonDao;
import model.AuthToken;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.AllPersonRequest;
import result.AllPersonResult;
import service.AllPersonService;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AllPersonServiceTest {
    private final User testUser = new User("sheila", "parker", "sheila@parker.com", "Sheila",
            "Parker", "f", "Sheila_Parker");
    private AuthToken testAuthToken;
    private Data data;
    private Database db;

    private static class Data {
        List<User> users;
        List<Person> persons;
        List<Person> Persons;

        public Data(List<User> users, List<Person> persons, List<Person> Persons) {
            this.users = users;
            this.persons = persons;
            this.Persons = Persons;
        }
    }


    @BeforeEach
    public void setUp() {
        try {
            db = new Database();
            Connection conn = db.getConnection();
            db.clearTables();
            PersonDao personDao = new PersonDao(conn);
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
            for (Person p : data.persons) {
                personDao.insert(p);
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
    public void getAllPeoplePass() {
        AllPersonRequest allPersonRequest = new AllPersonRequest(testAuthToken.getAuthtoken());
        AllPersonService allPersonService = new AllPersonService();
        AllPersonResult allPersonResult = allPersonService.getAllPeople(allPersonRequest);

        assertTrue(allPersonResult.isSuccess());
        List<Person> testPersons = allPersonResult.getData();
        assertNotNull(testPersons);

        //find all the people that should have been retrieved from the database
        List<Person> comparePersons = new ArrayList<>();
        for (Person p : data.persons) {
            if (p.getPersonID().equals(testUser.getPersonID())) {
                comparePersons.add(p);
            }
        }

        //remove all people that are the same, aka check that testPersons = comparePersons
        testPersons.retainAll(comparePersons);
        comparePersons.removeAll(testPersons);
        assertEquals(comparePersons.size(), 0);
    }

    @Test
    public void getAllPeopleFail() {
        //should send a bad request since the authtoken is not in the database
        AllPersonRequest allPersonRequest = new AllPersonRequest("not_a_good_authtoken");
        AllPersonService allPersonService = new AllPersonService();
        AllPersonResult allPersonResult = allPersonService.getAllPeople(allPersonRequest);

        assertFalse(allPersonResult.isSuccess());
        assertEquals(allPersonResult.getMessage(), "Error: Invalid auth token");
    }

}
