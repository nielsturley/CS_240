package model;

import java.util.Objects;

/**
 * Model class of User.
 */
public class User {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private String personID;

    /**
     * Creates a User.
     *
     * @param username unique username for user.
     * @param password user’s password.
     * @param email user's email.
     * @param firstName user's first name.
     * @param lastName user's last name.
     * @param gender user's gender, either 'm' or 'f'.
     * @param personID unique Person ID assigned to this user’s generated Person.
     */
    public User(String username, String password, String email, String firstName, String lastName, String gender, String personID) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.personID = personID;
    }

    /**
     * Compares the object with this User by value.
     *
     * @param o the compared-to object.
     * @return true if equivalent, false if not.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(email, user.email) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(gender, user.gender) && Objects.equals(personID, user.personID);
    }

    /**
     * Produces a string of the person.
     *
     * @return the string.
     */
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", personID='" + personID + '\'' +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
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

    public String getPersonID() {
        return personID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    /**
     * checks that the person has acceptable values
     * @return true if they are, false if not
     */
    public boolean valuesAreGood() {
        return username != null &&
                password != null &&
                email != null &&
                firstName != null &&
                lastName != null &&
                (gender.equals("m") || gender.equals("f")) &&
                personID != null;
    }
}
