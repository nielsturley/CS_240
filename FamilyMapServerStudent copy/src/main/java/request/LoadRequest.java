package request;

import model.Event;
import model.Person;
import model.User;

import java.util.List;

/**
 * LoadRequest class. Passed into LoadService functions.
 */
public class LoadRequest {
    private List<User> users;
    private List<Person> persons;
    private List<Event> events;

    /**
     * Creates a LoadRequest.
     *
     * @param users array of User to be loaded.
     * @param persons array of Person to be loaded.
     * @param events array of Event to be loaded.
     */
    public LoadRequest(List<User> users, List<Person> persons, List<Event> events) {
        this.users = users;
        this.persons = persons;
        this.events = events;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    /**
     * checks that the load has acceptable values
     * @return true if they are, false if not
     */
    public boolean valuesAreGood() {
        for (User u : users) {
            if (!u.valuesAreGood()) {
                return false;
            }
        }
        for (Person p : persons) {
            if (!p.valuesAreGood()) {
                return false;
            }
        }
        for (Event e : events) {
            if (!e.valuesAreGood()) {
                return false;
            }
        }
        return true;
    }
}
