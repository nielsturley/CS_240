package dao;

import model.User;

import java.sql.*;

/**
 * User Data Access class.
 */
public class UserDao {
    private final Connection conn;

    /**
     * Creates a User Data Access class.
     *
     * @param conn the connection.
     */
    public UserDao(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserts the given User into the database.
     *
     * @param user the user.
     * @throws DataAccessException if an SQL error occurs.
     */
    public void insert(User user) throws DataAccessException {
        String sql = "INSERT INTO User (Username, Password, Email, FirstName, LastName, Gender, PersonID) VALUES(?,?,?,?,?,?,?)";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setString(6, user.getGender());
            stmt.setString(7, user.getPersonID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                throw new DataAccessException("Error: Username already taken by another user");
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
     * Searches the database for the user (using their username).
     * @param username the username
     * @return User found
     * @throws DataAccessException if SQL error occurs
     */
    public User findFromUsername(String username) throws DataAccessException {
        ResultSet rs = null;
        String sql = "SELECT * FROM User WHERE Username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("Username"), rs.getString("Password"),
                        rs.getString("Email"), rs.getString("FirstName"), rs.getString("LastName"),
                        rs.getString("Gender"), rs.getString("PersonID"));
            }
            else {
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
     * Searches the database for the username + password combination.
     *
     * @param username the username.
     * @param password the password.
     * @return user if found, null if not.
     * @throws DataAccessException if an SQL error occurs.
     */
    public User validate(String username, String password) throws DataAccessException {
        ResultSet rs = null;
        String sql = "SELECT * FROM User WHERE Username = ? AND Password = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("Username"), rs.getString("Password"),
                        rs.getString("Email"), rs.getString("FirstName"), rs.getString("LastName"),
                        rs.getString("Gender"), rs.getString("PersonID"));
            }
            else {
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
     * Clears all users from the database.
     */
    public void clearAll() throws DataAccessException {
        String sql = "DELETE FROM User";
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
