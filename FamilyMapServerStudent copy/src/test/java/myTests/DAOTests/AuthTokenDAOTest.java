package myTests.DAOTests;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import model.AuthToken;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTokenDAOTest {
    private Database db;
    private User testUser;
    private AuthTokenDao authTokenDao;

    @BeforeEach
    public void setUp() {
        db = new Database();
        testUser = new User("sexybeast1", "1234567", "bob@thebuilder.com", "Dude",
                "Duder", "m", "00000");
        Connection conn = null;
        try {
            conn = db.getConnection();
            db.clearTables();
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
        authTokenDao = new AuthTokenDao(conn);
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
    public void generateTokenPass() {
        try {
            AuthToken token = authTokenDao.generateToken(testUser.getUsername());
            String compareTest = authTokenDao.findUser(token.getAuthtoken());
            assertNotNull(token);
            assertEquals(token.getUsername(), compareTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void generateTokenFail() {
        try {
            String nullTest = authTokenDao.findUser("not_a_good_authtoken");
            assertNull(nullTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findUserPass() {
        try {
            AuthToken authToken = authTokenDao.generateToken(testUser.getUsername());
            String retrieveTest = authTokenDao.findUser(authToken.getAuthtoken());
            assertNotNull(retrieveTest);
            assertEquals(authToken.getUsername(), retrieveTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findUserFail() {
        try {
            String nullTest = authTokenDao.findUser(testUser.getPersonID());
            assertNull(nullTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void clearPass() {
        try {
            authTokenDao.generateToken(testUser.getUsername());
            authTokenDao.clearAll();
            String nullTest = authTokenDao.findUser(testUser.getUsername());
            assertNull(nullTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void clearFail() {
        try {
            authTokenDao.clearAll();
            String nullTest = authTokenDao.findUser(testUser.getUsername());
            assertNull(nullTest);
        } catch (DataAccessException e) {
            fail(e.getMessage());
        }

    }
}

