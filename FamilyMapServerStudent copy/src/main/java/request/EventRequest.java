package request;

/**
 * EventRequest class. Passed into EventService functions.
 */
public class EventRequest {
    private String eventID;
    private String authtoken;

    /**
     * Creates a new EventRequest.
     *
     * @param eventID the eventID to be searched for.
     * @param authtoken the authtoken of the user.
     */
    public EventRequest(String eventID, String authtoken) {
        this.eventID = eventID;
        this.authtoken = authtoken;
    }

    public String getEventID() {
        return eventID;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }
}
