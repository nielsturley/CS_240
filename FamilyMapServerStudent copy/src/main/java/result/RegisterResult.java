package result;

/**
 * RegisterResult class returned to the RegisterHandler functions.
 */
public class RegisterResult {
    private String authtoken;
    private String username;
    private String personID;
    private String message;
    private boolean success;


    /**
     * Creates a new RegisterResult when an error occurs (error response).
     *
     * @param message description of the error.
     */
    public RegisterResult(String message) {
        this.message = message;
        this.success = false;

        authtoken = null;
        username = null;
        personID = null;
    }

    /**
     * Creates a new RegisterResult when no error occurs (success response).
     *
     * @param authtoken the user's authtoken.
     * @param username the user's username.
     * @param personID the requested personID.
     */
    public RegisterResult(String authtoken, String username, String personID) {
        this.authtoken = authtoken;
        this.username = username;
        this.personID = personID;
        this.success = true;

        message = null;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public String getUsername() {
        return username;
    }

    public String getPersonID() {
        return personID;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        if (success) {
            return "{\n" +
                    "\t\"authtoken\":\"" + authtoken + "\"\n" +
                    "\t\"username\":\"" + username + "\"\n" +
                    "\t\"personID\":\"" + personID + "\"\n" +
                    "\t\"success\":" + true + "\n" +
                    "}";
        }
        else {
            return "{\n" +
                    "\t\"message\":\"" + message + "\"\n" +
                    "\t\"success\":" + false + "\n" +
                    "}";
        }
    }
}
