package service;

import dao.*;
import model.Event;
import request.AllEventRequest;
import result.AllEventResult;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * AllEventService class. Passes AllEventRequest info into data access classes and returns AllEventResult.
 */
public class AllEventService {

    /**
     * Called to retrieve all events associated with a user from the database.
     *
     * @param e the AllEventRequest.
     * @return the AllEventResult, either error response or success response.
     */
    public AllEventResult getAllEvents(AllEventRequest e) {
        String token = e.getAuthtoken();
        Database database = new Database();
        Connection conn;
        try {
            conn = database.openConnection();

            //check authtoken database for the provided authtoken
            AuthTokenDao authDao = new AuthTokenDao(conn);
            String username;
            username = authDao.findUser(token);
            if (username == null) { //if there is no user for this authtoken
                database.closeConnection(false);
                return new AllEventResult("Error: Invalid auth token");
            }

            //begin finding all events associated with user
            EventDao eDao = new EventDao(conn);
            List<Event> events;
            events = new ArrayList<>(eDao.findForUser(username));

            database.closeConnection(false);
            return new AllEventResult(events);
        } catch (DataAccessException ex) {
            try {
                database.closeConnection(false);
            } catch (DataAccessException exc) {
                exc.printStackTrace();
            }
            return new AllEventResult(ex.getMessage());
        }
    }
}
