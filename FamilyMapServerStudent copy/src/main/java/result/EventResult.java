package result;

/**
 * EventResult class returned to EventHandler functions.
 */
public class EventResult {
    private String associatedUsername;
    private String eventID;
    private String personID;
    private float latitude;
    private float longitude;
    private String country;
    private String city;
    private String eventType;
    private int year;
    private boolean success;
    private String message;

    /**
     * Creates a new EventResult when an error occurs (error response).
     */
    public EventResult(String message) {
        this.message = message;
        success = false;
    }

    /**
     * Creates a new EventResult when no error occurs (success response).
     *
     * @param associatedUsername user's username.
     * @param eventID the returned event's ID.
     * @param personID the person's ID associated with the returned event.
     * @param latitude the returned event's latitude.
     * @param longitude the returned event's longitude.
     * @param country the returned event's country.
     * @param city the returned event's city.
     * @param eventType the returned event's type.
     * @param year the returned event's year.
     */
    public EventResult(String associatedUsername, String eventID, String personID,
                       float latitude, float longitude, String country,
                       String city, String eventType, int year) {
        this.associatedUsername = associatedUsername;
        this.eventID = eventID;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
        success = true;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public String getEventID() {
        return eventID;
    }

    public String getPersonID() {
        return personID;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() { return longitude; }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getEventType() {
        return eventType;
    }

    public int getYear() {
        return year;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
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
                    "\t\"associatedUsername\":\"" + associatedUsername + "\"\n" +
                    "\t\"eventID\":\"" + eventID + "\"\n" +
                    "\t\"personID\":\"" + personID + "\"\n" +
                    "\t\"latitude\":\"" + latitude + "\"\n" +
                    "\t\"longitude\":\"" + longitude + "\"\n" +
                    "\t\"country\":\"" + country + "\"\n" +
                    "\t\"city\":\"" + city + "\"\n" +
                    "\t\"eventType\":\"" + eventType + "\"\n" +
                    "\t\"year\":\"" + year + "\"\n" +
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
