package request;

/**
 * LoginRequest class. Passed into LoginService functions.
 */
public class LoginRequest {
    private String username;
    private String password;

    /**
     * Creates a LoginRequest.
     *
     * @param username the user's username.
     * @param password the user's password.
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * checks that the loginRequest has acceptable values
     * @return true if they are, false if not
     */
    public boolean valuesAreGood() {
        return username != null &&
                password != null;
    }
}
