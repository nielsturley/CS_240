package cs240.familymapclient;


import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import model.Event;
import model.Person;

public class DataCache {

    private static final DataCache instance = new DataCache();

    public static DataCache getInstance() {
        return instance;
    }

    private DataCache() {}

    private Map<String, Person> people;
    private Map<String, Event> events;
    private Map<String, List<Event>> personEvents;
    private Set<String> paternalAncestors;
    private Set<String> maternalAncestors;
    private Settings settings;
    private String currentUserID = null;
    private Person currentUserPerson;
    private Set<Float> availableColors;
    private Iterator<Float> currentColorIter;
    private Map<String, Float> eventColors;
    private List<Polyline> polylines;
    private List<Person> peopleCurrentlyDisplayed;


    /**
     * This will basically function as a constructor, as when I insert people and events I am specifying
     * a new set of data.
     *
     * @param people the new list of people
     * @param events the new list of events
     */
    public void insertPeopleEventData(List<Person> people, List<Event> events) {
        this.people = new HashMap<>();
        this.events = new HashMap<>();
        this.personEvents = new HashMap<>();
        this.paternalAncestors = new HashSet<>();
        this.maternalAncestors = new HashSet<>();
        this.availableColors = new LinkedHashSet<>();
        setColors();
        currentColorIter = availableColors.iterator();
        this.eventColors = new HashMap<>();
        this.polylines = new ArrayList<>();
        this.peopleCurrentlyDisplayed = new ArrayList<>();
        settings = new Settings();
        setPeople(people);
        setEvents(events);
        updateDisplayed();
    }

    public void logout() {
        this.people = null;
        this.events = null;
        this.personEvents = null;
        this.paternalAncestors = null;
        this.maternalAncestors = null;
        this.settings = null;
        this.currentUserID = null;
        this.currentUserPerson = null;
        this.availableColors = null;
        this.currentColorIter = null;
        this.eventColors = null;
        this.polylines = null;
        this.peopleCurrentlyDisplayed = null;
    }

    private void setPeople(List<Person> peopleToAdd) {
        for (Person p : peopleToAdd) {
            people.put(p.getPersonID(), p);
            if (p.getPersonID().equals(currentUserID)) {
                //setting the currentUser as this.currentUserPerson
                currentUserPerson = p;
            }
        }

        //begin recursive setting of paternal and maternal ancestors
        setPaternalAncestors(Objects.requireNonNull(people.get(currentUserPerson.getFatherID())));
        setMaternalAncestors(Objects.requireNonNull(people.get(currentUserPerson.getMotherID())));
    }

    private void setPaternalAncestors(Person paternalAncestor) {
        paternalAncestors.add(paternalAncestor.getPersonID());
        if (paternalAncestor.getFatherID() != null) {
            setPaternalAncestors(Objects.requireNonNull(people.get(paternalAncestor.getFatherID())));
        }
        if (paternalAncestor.getMotherID() != null) {
            setPaternalAncestors(Objects.requireNonNull(people.get(paternalAncestor.getMotherID())));
        }
    }

    private void setMaternalAncestors(Person maternalAncestor) {
        maternalAncestors.add(maternalAncestor.getPersonID());
        if (maternalAncestor.getFatherID() != null) {
            setMaternalAncestors(Objects.requireNonNull(people.get(maternalAncestor.getFatherID())));
        }
        if (maternalAncestor.getMotherID() != null) {
            setMaternalAncestors(Objects.requireNonNull(people.get(maternalAncestor.getMotherID())));
        }
    }

    private void setEvents(List<Event> eventsToAdd) {
        for (String p : people.keySet()) {
            //creating all the keys (i.e., personIDs) for the events map + initializing the arrayList
            //of associated events.
            personEvents.put(p, new ArrayList<>());
        }
        for (Event e : eventsToAdd) {
            String eventType = e.getEventType().toLowerCase();
            //checks to see if e's eventType already has a color assigned
            if (!eventColors.containsKey(eventType)) {
                if (!currentColorIter.hasNext()) {
                    //loop back to the start of available colors and begin again
                    currentColorIter = availableColors.iterator();
                }
                eventColors.put(eventType, currentColorIter.next());
            }


            events.put(e.getEventID(), e);
            Objects.requireNonNull(personEvents.get(e.getPersonID())).add(e);
        }
    }

    private void setColors() {
        float red = BitmapDescriptorFactory.HUE_RED;
        float orange = BitmapDescriptorFactory.HUE_ORANGE;
        float yellow = BitmapDescriptorFactory.HUE_YELLOW;
        float green = BitmapDescriptorFactory.HUE_GREEN;
        float cyan = BitmapDescriptorFactory.HUE_CYAN;
        float azure = BitmapDescriptorFactory.HUE_AZURE;
        float blue = BitmapDescriptorFactory.HUE_BLUE;
        float violet = BitmapDescriptorFactory.HUE_VIOLET;
        float magenta = BitmapDescriptorFactory.HUE_MAGENTA;
        float rose = BitmapDescriptorFactory.HUE_ROSE;

        availableColors.add(red);
        availableColors.add(orange);
        availableColors.add(yellow);
        availableColors.add(green);
        availableColors.add(cyan);
        availableColors.add(azure);
        availableColors.add(blue);
        availableColors.add(violet);
        availableColors.add(magenta);
        availableColors.add(rose);
    }

    public Person getPersonByID(String personID) {
        return people.get(personID);
    }

    public Event getEventByID(String eventID) {
        return events.get(eventID);
    }

    public List<Event> getPersonEvents(String personID) {
        List<Event> orderedEvents = new LinkedList<>();
        //insert above if year is greater, below if it is less than, or based on strings
        //if the year is the same. Special cases for birth and death (first and last).
        Event birth = null;
        Event death = null;
        if (personEvents.containsKey(personID)) {
            if (personEvents.get(personID) != null) {
                for (Event e : personEvents.get(personID)) {
                    //I'd hope the year comparing would cover this, but we'll be specific just in case.
                    if (e.getEventType().equals("Birth")) {
                        birth = e;
                        continue;
                    }
                    if (e.getEventType().equals("Death")) {
                        death = e;
                        continue;
                    }

                    if (orderedEvents.isEmpty()) {
                        orderedEvents.add(e);
                    } else {
                        boolean addedEvent = false;
                        for (int i = 0; i < orderedEvents.size(); ++i) {
                            if (e.getYear() < orderedEvents.get(i).getYear()) {
                                orderedEvents.add(i, e);
                                addedEvent = true;
                                break;
                            }
                            if (e.getYear() == orderedEvents.get(i).getYear()) {
                                if (e.getEventType().compareToIgnoreCase(orderedEvents.get(i).getEventType()) < 0) {
                                    orderedEvents.add(i,e);
                                    addedEvent = true;
                                    break;
                                }
                            }
                        }
                        if (!addedEvent) {
                            orderedEvents.add(e);
                        }
                    }
                }

                if (birth != null) {
                    orderedEvents.add(0, birth);
                }
                if (death != null) {
                    orderedEvents.add(death);
                }
                return orderedEvents;
            }
        }
        return null;
    }

    public List<Person> getPersonFamily(String personID) {
        List<Person> family = new ArrayList<>();

        //loops through all people. If p is a child of personID (p has mother/fatherID as personID)
        //add p to family
        for (Person p : people.values()) {
            if (p.getMotherID() != null) {
                if (p.getMotherID().equals(personID)) {
                    family.add(p);
                    continue;
                }
            }
            if (p.getFatherID() != null) {
                if (p.getFatherID().equals(personID)) {
                    family.add(p);
                }
            }
        }

        Person thisPerson = getPersonByID(personID);
        if (thisPerson.getSpouseID() != null) {
            family.add(getPersonByID(thisPerson.getSpouseID()));
        }
        if (thisPerson.getFatherID() != null) {
            family.add(getPersonByID(thisPerson.getFatherID()));
        }
        if (thisPerson.getMotherID() != null) {
            family.add(getPersonByID(thisPerson.getMotherID()));
        }

        return family;
    }

    public Event getEarliestEventForPerson(String personID) {
        Event earliestEvent = null;

        //loops through all of a person's events. If it's birth, automatically take that one. If
        //it's the first event in the for loop, set that one as earliest event and keep going till
        //(if) you find an earlier one.
        for (Event e : getPersonEvents(personID)) {
            if (e.getEventType().equals("Birth")) {
                earliestEvent = e;
                break;
            }
            if (earliestEvent == null) {
                earliestEvent = e;
                continue;
            }
            if (e.getYear() < earliestEvent.getYear()) {
                earliestEvent = e;
            }
        }
        return earliestEvent;
    }

    public List<Event> searchEvents(String query) {
        query = query.toLowerCase();
        List<Event> events = new ArrayList<>();
        for (Event e : this.events.values()) {
            //first check if the event is being filtered out
            if (isEventOnDisplay(e)) {
                //see if the event contains the same string (both lowercase so case doesn't matter)
                if (e.getCountry().toLowerCase().contains(query)) {
                    events.add(e);
                    continue;
                }
                if (e.getCity().toLowerCase().contains(query)) {
                    events.add(e);
                    continue;
                }
                if (e.getEventType().toLowerCase().contains(query)) {
                    events.add(e);
                    continue;
                }
                if (String.valueOf(e.getYear()).contains(query)) {
                    events.add(e);
                }
            }
        }
        return events;
    }

    public List<Person> searchPeople(String query) {
        query = query.toLowerCase();
        List<Person> people = new ArrayList<>();
        for (Person p : this.people.values()) {
            if (p.getFirstName().toLowerCase().contains(query)) {
                people.add(p);
                continue;
            }
            if (p.getLastName().toLowerCase().contains(query)) {
                people.add(p);
            }
        }
        return people;
    }

    public String getRelationship(Person personOnView, Person person) {
        if (personOnView.getSpouseID() != null) {
            if (personOnView.getSpouseID().equals(person.getPersonID())) {
                return "Spouse";
            }
        }

        if (personOnView.getMotherID() != null) {
            if (personOnView.getMotherID().equals(person.getPersonID())) {
                return "Mother";
            }
        }

        if (personOnView.getMotherID() != null) {
            if (personOnView.getFatherID().equals(person.getPersonID())) {
                return "Father";
            }
        }

        if (person.getMotherID().equals(personOnView.getPersonID()) ||
                person.getFatherID().equals(personOnView.getPersonID())) {
            return "Child";
        }

        return "ERROR! No relationship found!";
    }

    public List<Person> getPeopleCurrentlyDisplayed() {
        return peopleCurrentlyDisplayed;
    }

    public Set<String> getPaternalAncestors() {
        return paternalAncestors;
    }

    public Set<String> getMaternalAncestors() {
        return maternalAncestors;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setCurrentUserID(String userID) {
        this.currentUserID = userID;
    }

    public String getCurrentUserID() {
        return currentUserID;
    }

    public Person getCurrentUserPerson() {
        return currentUserPerson;
    }

    public Map<String, Float> getEventColors() {
        return eventColors;
    }

    public List<Polyline> getPolylines() {
        return polylines;
    }

    public void addPolyline(Polyline line) {
        polylines.add(line);
    }

    public void updateDisplayed() {
        peopleCurrentlyDisplayed.clear();
        for (Person p : people.values()) {
            if (isPersonOnDisplay(p)) {
                peopleCurrentlyDisplayed.add(p);
            }
        }
    }

    public boolean isEventOnDisplay(Event e) {
        return peopleCurrentlyDisplayed.contains(getPersonByID(e.getPersonID()));
    }

    public boolean isPersonOnDisplay(Person p) {
        return (maternalAncestors.contains(p.getPersonID()) && settings.isMaternalSideFilterOn() ||
                paternalAncestors.contains(p.getPersonID()) && settings.isPaternalSideFilterOn() ||
                !maternalAncestors.contains(p.getPersonID()) && !paternalAncestors.contains(p.getPersonID()))
                &&
                (p.getGender().equals("m") && settings.isMaleFilterOn() ||
                p.getGender().equals("f") && settings.isFemaleFilterOn());
    }
}
