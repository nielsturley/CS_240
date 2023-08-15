package dao;

import model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Event Data Access class.
 */
public class EventDao {
    private final Connection conn;

    /**
     * Creates an Event Data Access class.
     *
     * @param conn the connection.
     */
    public EventDao(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserts the given event into the database.
     *
     * @param event the event.
     * @throws DataAccessException if an SQL error occurs.
     */
    public void insert(Event event) throws DataAccessException {
        String sql = "INSERT INTO Event (EventID, AssociatedUsername, PersonID, Latitude, Longitude, " +
                "Country, City, EventType, Year) VALUES(?,?,?,?,?,?,?,?,?)";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, event.getEventID());
            stmt.setString(2, event.getAssociatedUsername());
            stmt.setString(3, event.getPersonID());
            stmt.setFloat(4, event.getLatitude());
            stmt.setFloat(5, event.getLongitude());
            stmt.setString(6, event.getCountry());
            stmt.setString(7, event.getCity());
            stmt.setString(8, event.getEventType());
            stmt.setInt(9, event.getYear());

            stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                throw new DataAccessException("Error: Event already exists");
            }
            else {
                throw new DataAccessException("Error: Internal server error");
            }
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Searches the database for an event.
     *
     * @param eventID the ID searched for.
     * @return the Event found.
     * @throws DataAccessException if an SQL error occurs.
     */
    public Event find(String eventID) throws DataAccessException {
        Event event;
        ResultSet rs = null;
        String sql = "SELECT * FROM Event WHERE EventID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                event = new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
                return event;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error: Internal server error");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * Searches the database for all events associated with a user.
     *
     * @param username the username searched for.
     * @return a List of all events associated with that user.
     * @throws DataAccessException if an SQL error occurs.
     */
    public List<Event> findForUser(String username) throws DataAccessException {
        List<Event> associatedEvents = new ArrayList<>();
        ResultSet rs = null;
        String sql = "SELECT * FROM Event WHERE AssociatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            while (rs.next()) {
                associatedEvents.add(new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year")));
            }
            if (associatedEvents.isEmpty()) {
                return null;
            } else {
                return associatedEvents;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error: Internal server error");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * Clears the database of all events associated with the given username.
     *
     * @param username the username
     * @throws DataAccessException if a SQL error occurs
     */
    public void clearForUser(String username) throws DataAccessException {
        String sql = "DELETE FROM Event WHERE AssociatedUsername = ?;";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error: Internal server error");
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Clears all events from the database.
     */
    public void clearAll() throws DataAccessException {
        String sql = "DELETE FROM Event";
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error: Internal server error");
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
