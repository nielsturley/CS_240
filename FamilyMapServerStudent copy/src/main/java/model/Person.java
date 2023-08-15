package model;

import java.util.Objects;

/**
 * Model class of Person
 */
public class Person {
    private String personID;
    private String associatedUsername;
    private String firstName;
    private String lastName;
    private String gender;
    private String fatherID;
    private String motherID;
    private String spouseID;

    /**
     * Creates a Person.
     *
     * @param personID unique identifier for this person.
     * @param associatedUsername username of user to which this person belongs.
     * @param firstName person’s first name.
     * @param lastName person’s last name.
     * @param gender person’s gender, either 'm' or 'f'.
     * @param fatherID person ID of person’s father, may be null.
     * @param motherID person ID of person’s mother, may be null.
     * @param spouseID person ID of person’s spouse, may be null.
     */
    public Person(String personID, String associatedUsername, String firstName, String lastName,
                  String gender, String fatherID, String motherID, String spouseID) {
        this.personID = personID;
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
    }

    /**
     * Compares the object with this Person by value.
     *
     * @param o the compared-to object.
     * @return true if equivalent, false if not.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return personID.equals(person.personID) &&
                associatedUsername.equals(person.associatedUsername) &&
                firstName.equals(person.firstName) && lastName.equals(person.lastName) &&
                gender.equals(person.gender) && Objects.equals(fatherID, person.fatherID) &&
                Objects.equals(motherID, person.motherID) &&
                Objects.equals(spouseID, person.spouseID);
    }

    /**
     * Produces a string of the person.
     *
     * @return the string.
     */
    @Override
    public String toString() {
        return "Person{" +
                "personID='" + personID + '\'' +
                ", associatedUsername='" + associatedUsername + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", fatherID='" + fatherID + '\'' +
                ", motherID='" + motherID + '\'' +
                ", spouseID='" + spouseID + '\'' +
                '}';
    }

    public String getPersonID() {
        return personID;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
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

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
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

    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
    }

    /**
     * checks that the person has acceptable values
     * @return true if they are, false if not
     */
    public boolean valuesAreGood() {
        return personID != null &&
                associatedUsername != null &&
                firstName != null &&
                lastName != null &&
                (gender.equals("m") || gender.equals("f"));
    }
}
