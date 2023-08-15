package request;

/**
 * AllPersonRequest class. Passed into AllPersonService functions.
 */
public class AllPersonRequest {
    private String authtoken;

    /**
     * Creates a new AllPersonRequest.
     *
     * @param authtoken the authtoken of the user.
     */
    public AllPersonRequest(String authtoken) {
        this.authtoken = authtoken;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }
}
