package myTests.ServiceTests;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.UserDao;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import result.LoginResult;
import service.LoginService;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {
    private Database db;
    private final User testUser = new User("sheila", "parker", "sheila@parker.com",
            "Sheila", "Parker", "f", "Sheila_Parker");

    @BeforeEach
    public void setUp() {
        try {
            db = new Database();
            Connection conn = db.openConnection();
            db.clearTables();
            UserDao userDao = new UserDao(conn);
            userDao.insert(testUser);
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
    public void loginPass() {
        LoginRequest loginRequest = new LoginRequest(testUser.getUsername(), testUser.getPassword());
        LoginService loginService = new LoginService();
        LoginResult loginResult = loginService.login(loginRequest);

        assertTrue(loginResult.isSuccess());

        try {
            //check to see if user is now in the authtoken database
            Connection conn = db.getConnection();
            AuthTokenDao authTokenDao = new AuthTokenDao(conn);
            String username = authTokenDao.findUser(loginResult.getAuthtoken());
            assertEquals(testUser.getUsername(), username);
            db.closeConnection(false);
        } catch (DataAccessException e){
            fail(e.getMessage());
        }
    }

    @Test
    public void loginFail() {
        LoginRequest loginRequest = new LoginRequest("not_a_good_username", "not_a_good_password");
        LoginService loginService = new LoginService();
        LoginResult loginResult = loginService.login(loginRequest);

        assertFalse(loginResult.isSuccess());
        assertEquals(loginResult.getMessage(), "Error: Request property missing or has invalid value");
    }

}
