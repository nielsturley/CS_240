package service;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.EventDao;
import model.Event;
import request.EventRequest;
import result.EventResult;

import java.sql.Connection;

/**
 * EventService class. Passes EventRequest info into data access classes and returns EventResult.
 */
public class EventService {

    /**
     * Called to retrieve an event from the database.
     *
     * @param e the EventRequest.
     * @return the EventResult, either error response or success response.
     */
    public EventResult getEvent(EventRequest e) {
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
                return new EventResult("Error: Invalid auth token");
            }

            //begin finding the event
            EventDao eventDao = new EventDao(conn);
            Event event;
            event = eventDao.find(e.getEventID());
            if (event == null) { //if there is no event with that eventID
                database.closeConnection(false);
                return new EventResult("Error: Invalid eventID parameter");
            }
            if (!event.getAssociatedUsername().equals(username)) { //if requested event doesn't belong to the user
                database.closeConnection(false);
                return new EventResult("Error: Requested event does not belong to this user");
            }

            database.closeConnection(false);
            return new EventResult(event.getAssociatedUsername(), event.getEventID(),
                    event.getPersonID(), event.getLatitude(), event.getLongitude(),
                    event.getCountry(), event.getCity(), event.getEventType(), event.getYear());
        } catch (DataAccessException ex) {
            try {
                database.closeConnection(false);
            } catch (DataAccessException exc) {
                exc.printStackTrace();
            }
            return new EventResult(ex.getMessage());
        }
    }
}
