package model;

import java.util.Objects;

/**
 * Model class of authtoken.
 */
public class AuthToken {
    private String authtoken;
    private String username;

    /**
     * Creates an AuthToken.
     *
     * @param authtoken unique authtoken.
     * @param username username that is associated with the authtoken.
     */
    public AuthToken(String authtoken, String username) {
        this.authtoken = authtoken;
        this.username = username;
    }

    /**
     * Produces a string of the authtoken.
     *
     * @return the string.
     */
    @Override
    public String toString() {
        return "AuthToken{" +
                "authtoken='" + authtoken + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    /**
     * Compares an object with this authtoken by value.
     *
     * @param o the compared to object.
     * @return true if equivalent, false if not.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthToken authtoken1 = (AuthToken) o;
        return Objects.equals(authtoken, authtoken1.authtoken) && Objects.equals(username, authtoken1.username);
    }


    public String getAuthtoken() {
        return authtoken;
    }

    public String getUsername() {
        return username;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
