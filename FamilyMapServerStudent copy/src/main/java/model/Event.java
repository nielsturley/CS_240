package model;

/**
 * Model class of Event.
 */
public class Event {
    private String eventID;
    private String associatedUsername;
    private String personID;
    private final float latitude;
    private final float longitude;
    private final String country;
    private final String city;
    private final String eventType;
    private int year;

    /**
     * Creates an Event.
     *
     * @param eventID unique identifier for this event.
     * @param associatedUsername username of user to which this event belongs.
     * @param personID ID of person to which this event belongs.
     * @param latitude latitude of event’s location.
     * @param longitude longitude of event’s location.
     * @param country country in which event occurred.
     * @param city city in which event occurred.
     * @param eventType type of event.
     * @param year year in which event occurred.
     */
    public Event(String eventID, String associatedUsername, String personID, float latitude,
                 float longitude, String country, String city, String eventType, int year) {
        this.eventID = eventID;
        this.associatedUsername = associatedUsername;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }

    /**
     * Compares an object with this event by value.
     *
     * @param o the compared-to object.
     * @return true if equivalent, false if not.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return eventID.equals(event.eventID) &&
                associatedUsername.equals(event.associatedUsername) &&
                personID.equals(event.personID) && latitude == event.latitude &&
                longitude == event.longitude && country.equals(event.country) &&
                city.equals(event.city) && eventType.equals(event.eventType) &&
                year == event.year;
    }

    /**
     * Produces a string of the event.
     *
     * @return the string.
     */
    @Override
    public String toString() {
        return "Event{" +
                "eventID='" + eventID + '\'' +
                ", associatedUsername='" + associatedUsername + '\'' +
                ", personID='" + personID + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", eventType='" + eventType + '\'' +
                ", year=" + year +
                '}';
    }

    public String getEventID() {
        return eventID;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public String getPersonID() {
        return personID;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

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

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public void setYear(int year) {
        this.year = year;
    }

    /**
     * check that the event has acceptable values
     * @return true if they are, false if not
     */
    public boolean valuesAreGood() {
        return eventID != null &&
                personID != null &&
                latitude != 0.0f &&
                longitude != 0.0f &&
                country != null &&
                city != null &&
                eventType != null &&
                year != 0;
    }
}
