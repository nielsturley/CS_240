package myTests.DAOTests;

import dao.DataAccessException;
import dao.Database;
import dao.UserDao;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private Database db;
    private User testUser;
    private UserDao uDao;

    @BeforeEach
    public void setUp() {
        try {
            db = new Database();
            testUser = new User("sheila", "parker", "sheila@parker.com", "Sheila",
                    "Parker", "f", "Sheila_Parker");
            Connection conn = db.getConnection();
            db.clearTables();
            uDao = new UserDao(conn);
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
            uDao.insert(testUser);
            User compareTest = uDao.findFromUsername(testUser.getUsername());
            assertNotNull(compareTest);
            assertEquals(testUser, compareTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void insertFail() {
        try {
            uDao.insert(testUser);
            assertThrows(DataAccessException.class, ()-> uDao.insert(testUser));
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findPass() {
        try {
            uDao.insert(testUser);
            User retrieveTest = uDao.findFromUsername(testUser.getUsername());
            assertNotNull(retrieveTest);
            assertEquals(testUser, retrieveTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findFail() {
        try {
            User nullTest = uDao.findFromUsername(testUser.getUsername());
            assertNull(nullTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void clearPass() {
        try {
            uDao.insert(testUser);
            uDao.clearAll();
            User nullTest = uDao.findFromUsername(testUser.getUsername());
            assertNull(nullTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void clearFail() {
        try {
            uDao.clearAll();
            User nullTest = uDao.findFromUsername(testUser.getUsername());
            assertNull(nullTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findFromUsernamePass() {
        try {
            uDao.insert(testUser);
            User user = uDao.findFromUsername(testUser.getUsername());
            assertNotNull(user);
            assertEquals(user, testUser);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findFromUsernameFail() {
        try {
            User user = uDao.findFromUsername(testUser.getUsername());
            assertNull(user);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void validatePass() {
        try {
            uDao.insert(testUser);
            User user = uDao.validate(testUser.getUsername(), testUser.getPassword());
            assertNotNull(user);
            assertEquals(testUser, user);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void validateFail() {
        try {
            User user = uDao.validate(testUser.getUsername(), testUser.getPassword());
            assertNull(user);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void clearAllPass() {
        try {
            uDao.insert(testUser);
            uDao.clearAll();
            User nullTest = uDao.findFromUsername(testUser.getUsername());
            assertNull(nullTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void clearAllFail() {
        try {
            uDao.clearAll();
            User nullTest = uDao.findFromUsername(testUser.getUsername());
            assertNull(nullTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }
}
