package result;

/**
 * PersonResult class returned to the PersonHandler functions.
 */
public class PersonResult {
    private String associatedUsername;
    private String personID;
    private String firstName;
    private String lastName;
    private String gender;
    private String fatherID;
    private String motherID;
    private String spouseID;
    private boolean success;
    private String message;

    /**
     * Creates a new PersonResult when an error occurs (error response).
     *
     * @param message description of the error.
     */
    public PersonResult(String message) {
        this.message = message;
        success = false;
    }

    /**
     * Creates a new PersonResult when no error occurs (success response).
     *
     * @param associatedUsername user's username.
     * @param personID the returned person's ID.
     * @param firstName the returned person's first name.
     * @param lastName the returned person's last name.
     * @param gender the returned person's gender, either 'm' or 'f'.
     * @param fatherID the returned person's father's ID, may be null.
     * @param motherID the returned person's mother's ID, may be null.
     * @param spouseID the returned person's spouse's ID, may be null.
     */
    public PersonResult(String associatedUsername, String personID,
                        String firstName, String lastName, String gender,
                        String fatherID, String motherID, String spouseID) {
        this.associatedUsername = associatedUsername;
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
        success = true;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public String getPersonID() {
        return personID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getFatherID() {
        return fatherID;
    }

    public String getMotherID() {
        return motherID;
    }

    public String getSpouseID() {
        return spouseID;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(String gender) {
        this.gender = gender;
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
                "\t\"associatedUsername\":\"" + associatedUsername + "\"\n" +
                "\t\"personID\":\"" + personID + "\"\n" +
                "\t\"firstName\":\"" + firstName + "\"\n" +
                "\t\"lastName\":\"" + lastName + "\"\n" +
                "\t\"gender\":\"" + gender + "\"\n" +
                "\t\"fatherID\":\"" + fatherID + "\"\n" +
                "\t\"motherID\":\"" + motherID + "\"\n" +
                "\t\"spouseID\":\"" + spouseID + "\"\n" +
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
