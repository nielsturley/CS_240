package service;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.UserDao;
import model.AuthToken;
import model.User;
import request.LoginRequest;
import result.LoginResult;

import java.sql.Connection;

/**
 * LoginService class. Passes LoginRequest info into data access classes and returns LoginResult.
 */
public class LoginService {

    /**
     * Called to log in a user.
     *
     * @param l the LoginRequest.
     * @return the LoginResult, either error response or success response.
     */
    public LoginResult login(LoginRequest l) {
        Database database = new Database();
        Connection conn;
        try {
            conn = database.openConnection();

            //check to make sure the login information is correct
            UserDao userDao = new UserDao(conn);
            User verifiedUser;
            verifiedUser = userDao.validate(l.getUsername(), l.getPassword());
            if (verifiedUser != null) {
                //the login information is correct, so create new authtoken
                AuthTokenDao authTokenDao = new AuthTokenDao(conn);
                AuthToken authToken;
                authToken = authTokenDao.generateToken(l.getUsername());

                database.closeConnection(true);
                return new LoginResult(authToken.getAuthtoken(), verifiedUser.getUsername(), verifiedUser.getPersonID());
            } else {
                database.closeConnection(false);
                return new LoginResult("Error: Request property missing or has invalid value");
            }
        } catch (DataAccessException e) {
            try {
                database.closeConnection(false);
            } catch (DataAccessException exc) {
                exc.printStackTrace();
            }
            return new LoginResult(e.getMessage());
        }
    }
}
