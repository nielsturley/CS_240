package dao;

import java.sql.*;
import java.util.UUID;

import model.AuthToken;

/**
 * AuthToken Data Access class.
 */
public class AuthTokenDao {
    private final Connection conn;

    /**
     * Creates an AuthToken Data Access class.
     *
     * @param conn the connection.
     */
    public AuthTokenDao(Connection conn) {
        this.conn = conn;
    }

    /**
     * Generates an AuthToken using the username. Inserts the AuthToken into the database
     * and returns the AuthToken.
     *
     * @param username the username on login.
     * @return the created AuthToken.
     * @throws DataAccessException if an SQL error occurs.
     */
    public AuthToken generateToken(String username) throws DataAccessException {
        AuthToken token = new AuthToken(UUID.randomUUID().toString(), username);
        insert(token);
        return token;
    }

    /**
     * Called when a token is generated. Inserts the AuthToken into the database.
     *
     * @param token the AuthToken created for the user.
     * @throws DataAccessException if an SQL error occurs.
     */
    private void insert(AuthToken token) throws DataAccessException {
        String sql = "INSERT INTO Authtoken (Authtoken, Username) VALUES(?,?)";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, token.getAuthtoken());
            stmt.setString(2, token.getUsername());

            stmt.executeUpdate();
        } catch (SQLException e) {
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
     * Searches the database for the token provided.
     *
     * @param token the AuthToken searched for.
     * @return the username found.
     * @throws DataAccessException if a SQL error occurs
     */
    public String findUser(String token) throws DataAccessException {
        String username;
        ResultSet rs = null;
        String sql = "SELECT * FROM Authtoken WHERE Authtoken = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            rs = stmt.executeQuery();
            if (rs.next()) {
                username = rs.getString("Username");
                return username;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error: Internal server error");
        } finally {
            if (rs != null) {
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
     * Clears all AuthTokens from the database.
     *
     * @throws DataAccessException if an SQL error occurs.
     */
    public void clearAll() throws DataAccessException {
        String sql = "DELETE FROM Authtoken";
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
