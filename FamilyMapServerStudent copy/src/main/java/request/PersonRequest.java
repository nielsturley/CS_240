package request;

/**
 * PersonRequest class. Passed into PersonService functions.
 */
public class PersonRequest {
    private String personID;
    private String authtoken;

    /**
     * Creates a new PersonRequest.
     *
     * @param personID the personID to be searched for.
     * @param authtoken the authtoken of the user.
     */
    public PersonRequest(String personID, String authtoken) {
        this.personID = personID;
        this.authtoken = authtoken;
    }

    public String getPersonID() {
        return personID;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }
}
