package service;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.UserDao;
import model.AuthToken;
import model.User;
import request.FillRequest;
import request.RegisterRequest;
import result.FillResult;
import result.RegisterResult;

import java.sql.Connection;
import java.util.UUID;

/**
 * RegisterService class. Passes RegisterRequest info into data access classes and returns RegisterResult.
 */
public class RegisterService {
    private static final int DEFAULT_GENERATIONS = 4;
    /**
     * Called to register a new user.
     *
     * @param r the RegisterRequest.
     * @return the RegisterResult, either error response or success response.
     */
    public RegisterResult register(RegisterRequest r) {
        Database database = new Database();
        Connection conn;
        try {
            conn = database.openConnection();

            //create and insert new user
            String personID = UUID.randomUUID().toString();
            User user = new User(r.getUsername(), r.getPassword(), r.getEmail(), r.getFirstName(), r.getLastName(), r.getGender(), personID);
            UserDao userDao = new UserDao(conn);
            userDao.insert(user);
            database.closeConnection(true);

            //begin FillRequest (will create 4 generations)
            FillRequest fillRequest = new FillRequest(r.getUsername(), DEFAULT_GENERATIONS);
            FillService fillService = new FillService();
            FillResult fillResult = fillService.fill(fillRequest);
            if (!fillResult.isSuccess()) {
                database.closeConnection(false);
                return new RegisterResult(fillResult.getMessage());
            }

            //create authtoken for user
            conn = database.openConnection();
            AuthTokenDao authTokenDao = new AuthTokenDao(conn);
            AuthToken token;
            token = authTokenDao.generateToken(r.getUsername());

            database.closeConnection(true);
            return new RegisterResult(token.getAuthtoken(), r.getUsername(), personID);
        } catch (DataAccessException ex) {
            try {
                database.closeConnection(false);
            } catch (DataAccessException exc) {
                exc.printStackTrace();
            }
            return new RegisterResult(ex.getMessage());
        }
    }
}
