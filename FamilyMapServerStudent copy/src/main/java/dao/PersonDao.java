package dao;

import model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Person Data Access class.
 */
public class PersonDao {
    private final Connection conn;

    /**
     * Creates a Person Data Access class.
     *
     * @param conn the connection.
     */
    public PersonDao(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserts the given Person into the database.
     *
     * @param person the person.
     */
    public void insert(Person person) throws DataAccessException {
        String sql = "INSERT INTO Person (PersonID, AssociatedUsername, FirstName, LastName, Gender, FatherID, MotherID, SpouseID) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, person.getPersonID());
            stmt.setString(2, person.getAssociatedUsername());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4, person.getLastName());
            stmt.setString(5, person.getGender());
            stmt.setString(6, person.getFatherID());
            stmt.setString(7, person.getMotherID());
            stmt.setString(8, person.getSpouseID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: Internal server error");
        }
    }

    /**
     * Searches the database for the personID.
     *
     * @param personID the searched for personID.
     * @return the found Person
     * @throws DataAccessException if an SQL error occurs.
     */
    public Person find(String personID) throws DataAccessException {
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM Person WHERE PersonID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                person = new Person(rs.getString("PersonID"), rs.getString("AssociatedUsername"),
                        rs.getString("FirstName"), rs.getString("LastName"), rs.getString("Gender"),
                        rs.getString("FatherID"), rs.getString("MotherID"), rs.getString("SpouseID"));
                return person;
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                throw new DataAccessException("Error: Person already exists");
            }
            else {
                throw new DataAccessException("Error: Internal server error");
            }
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * Searches the database for all people associated with a user.
     * @param username the username searched for.
     * @return a List of all people associated with a user.
     * @throws DataAccessException if an SQL error occurs.
     */
    public List<Person> findForUser(String username) throws DataAccessException {
        List<Person> associatedPeople = new ArrayList<>();
        ResultSet rs = null;
        String sql = "SELECT * FROM Person WHERE AssociatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            while (rs.next()) {
                associatedPeople.add(new Person(rs.getString("PersonID"), rs.getString("AssociatedUsername"),
                        rs.getString("FirstName"), rs.getString("LastName"), rs.getString("Gender"),
                        rs.getString("FatherID"), rs.getString("MotherID"), rs.getString("SpouseID")));
            }
            if (associatedPeople.isEmpty()) {
                return null;
            }
            else {
                return associatedPeople;
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
     * Clears the database of all people associated with the given username
     *
     * @param username the username
     * @throws DataAccessException if a SQL error occrus
     */
    public void clearForUser(String username) throws DataAccessException {
        PreparedStatement stmt = null;
        String sql = "DELETE FROM Person WHERE AssociatedUsername = ?;";
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
     * Clears all people from the database.
     *
     * @throws DataAccessException if an SQL error occurs.
     */
    public void clearAll() throws DataAccessException {
        String sql = "DELETE FROM Person";
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error: internal server error");
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
