package request;

/**
 * AllEventRequest class. Passed into AllEventService functions.
 */
public class AllEventRequest {
    private String authtoken;

    /**
     * Creates a new AllEventRequest.
     *
     * @param authtoken the authtoken of the user.
     */
    public AllEventRequest(String authtoken) {
        this.authtoken = authtoken;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }
}
