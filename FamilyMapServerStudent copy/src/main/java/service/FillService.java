package service;


import dao.*;
import json.JsonDeserializer;
import json.Location;
import json.Locations;
import json.Names;
import model.Event;
import model.Person;
import model.User;
import request.FillRequest;
import result.FillResult;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;

/**
 * FillService class. Passes FillRequest info into data access classes and returns FillResult.
 */
public class FillService {
    private Names fnames;
    private Names mnames;
    private Names snames;
    private Locations locations;
    private int rootPersonBirth; //this is the birthday of a child of two parents. Used to generate the parent's events.
    private User currentUser;
    private List<Person> currentGenerationPeople; //this is the set of 'children' that will have parents generated for them
    private final List<Event> eventsToAdd;
    private final List<Person> peopleToAdd;
    private static final int MIN_MARRIAGE_AGE = 13;
    private static final int MIN_PREGNANT_AGE = 13;
    private static final int MAX_PREGNANT_AGE = 50;
    private static final int MAX_DEATH_AGE = 120;

    public FillService() {
        currentUser = null;
        currentGenerationPeople = new ArrayList<>();
        eventsToAdd = new ArrayList<>();
        peopleToAdd = new ArrayList<>();

        //parse provided random names/locations
        JsonDeserializer json = new JsonDeserializer();
        try {
            fnames = json.parseNames(new File("json/fnames.json"));
            mnames = json.parseNames(new File ("json/mnames.json"));
            snames = json.parseNames(new File ("json/snames.json"));
            locations = json.parseLocations(new File ("json/locations.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called to fill in new generations for in a user. Clears old generations and replaces them with
     * the specified number of generations (default is 4).
     *
     * @param f the FillRequest.
     * @return the FillResult, either error response or success response.
     */
    public FillResult fill(FillRequest f) {
        Database database = new Database();
        Connection conn;
        try {
            conn = database.openConnection();

            //clear events and people previously associated with this user
            EventDao eventDao = new EventDao(conn);
            eventDao.clearForUser(f.getUsername());
            PersonDao personDao = new PersonDao(conn);
            personDao.clearForUser(f.getUsername());



            //begin user-specific creation (i.e., new person, new birth event)
            UserDao userDao = new UserDao(conn);
            currentUser = userDao.findFromUsername(f.getUsername());
            if (currentUser == null) {
                database.closeConnection(false);
                return new FillResult("Invalid username or generations parameter", false);
            }

            //create person for currentUser
            Person person = new Person(currentUser.getPersonID(), currentUser.getUsername(), currentUser.getFirstName(),
                    currentUser.getLastName(), currentUser.getGender(), null, null, null);
            peopleToAdd.add(person);
            currentGenerationPeople.add(person);

            //create birth event for currentUser
            Event birth = createEvent("Birth", person);
            rootPersonBirth = birth.getYear();
            eventsToAdd.add(birth);

            //end of user-specific creation


            //begin creating the people and events for the number of specified generations
            for (int i = 0; i < f.getGenerations(); ++i) {
                List<Person> newParents = createParents();
                createAllEvents(newParents);
                peopleToAdd.addAll(newParents);
                currentGenerationPeople = newParents;
            }
            //end creating the people and events for the number of specified generations


            //insert all the created people and events into the database
            for (Person p : peopleToAdd) {
                personDao.insert(p);
            }
            for (Event e : eventsToAdd) {
                eventDao.insert(e);
            }

            database.closeConnection(true);
            return new FillResult("Successfully added " + peopleToAdd.size() + " persons and " +
                    eventsToAdd.size() + " events to the database.", true);
        } catch (DataAccessException ex) {
            try {
                database.closeConnection(false);
            } catch (DataAccessException exc) {
                ex.printStackTrace();
            }
            return new FillResult(ex.getMessage(), false);
        }
    }

    /**
     * Create birth, marriage, and death for each person in newParents
     * @param newParents the list of new parents
     */
    private void createAllEvents(List<Person> newParents) {
        for (Person p : newParents) {

            //find who the child is of this person
            for (Person isThisMyChild : currentGenerationPeople) {

                //if isThisMyChild person (from currentGenerationPeople) is the child of p
                if (isThisMyChild.getFatherID().equals(p.getPersonID()) || isThisMyChild.getMotherID().equals(p.getPersonID())) {

                    //find the child's birth (used to generate the parent's events)
                    for (Event e : eventsToAdd)  {
                        if (e.getPersonID().equals(isThisMyChild.getPersonID()) && e.getEventType().equals("Birth")) {
                            //set as rootPersonBirth
                            rootPersonBirth = e.getYear();
                            break;
                        }
                    }
                    break;
                }
            }
            Event birth = createEvent("Birth", p);
            eventsToAdd.add(birth);
            Event marriage = createEvent("Marriage", p, birth.getYear());
            eventsToAdd.add(marriage);
            Event death = createEvent("Death", p, birth.getYear());
            eventsToAdd.add(death);
        }
    }

    private Event createEvent(String eventType, Person p, int yearData) {
        String eventID = UUID.randomUUID().toString();
        Location location = locations.pickRandomLocation();

        int year = -999999; //initialized to an impossible year for no errors, even though it will be set

        switch (eventType) {
            case "Birth" :
                if (p.getPersonID().equals(currentUser.getPersonID())) {
                    //random number between 0 and 2023
                    year = new Random().nextInt(2023);
                }
                else {
                    int maxAge = rootPersonBirth - MIN_PREGNANT_AGE;
                    int minAge = rootPersonBirth - MAX_PREGNANT_AGE;

                    //random number between 13 years ago and 50 years ago
                    year = new Random().nextInt(maxAge - minAge) + minAge;
                }
                break;
            case "Marriage" :
                boolean alreadyMarried = false;
                int thisBirthYear = -999999; //initialized to an impossible year (not 0, since that's valid) just for no errors, even though it will be set

                //search created events for this person's birth year. Will always find set year (birth generates first)
                for (Event e : eventsToAdd) {
                    if (e.getPersonID().equals(p.getPersonID()) && e.getEventType().equals("Birth")) {
                        thisBirthYear = e.getYear();
                        break;
                    }
                }

                //search created events for the potentially already generated marriage year.
                for (Event e : eventsToAdd) {
                    if (e.getPersonID().equals(p.getSpouseID()) && e.getEventType().equals("Marriage")) {
                        alreadyMarried = true;
                        int marriageYear = e.getYear();
                        if (marriageYear < (thisBirthYear + MIN_MARRIAGE_AGE)) { //if already generated marriage is less than realistic for this person
                            marriageYear = thisBirthYear + MIN_MARRIAGE_AGE; //set to a higher, realistic year
                            e.setYear(marriageYear);
                        }
                        year = marriageYear;
                        location = new Location(e.getCountry(), e.getCity(), e.getLatitude(), e.getLongitude());
                        break;
                    }
                }

                //if there is no already generated marriage year
                if (!alreadyMarried) {
                    int maxAge = rootPersonBirth;
                    int minAge = yearData + MIN_MARRIAGE_AGE;

                    //random number between child's birth (Law of Chastity baby) and (this person's birth + 13)
                    year = new Random().nextInt(maxAge - minAge) + minAge;
                }
                break;
            case "Death" :
                int maxAge = yearData + MAX_DEATH_AGE;
                int minAge = rootPersonBirth;

                //random number between child's birth and 120 years from birth
                year = new Random().nextInt(maxAge - minAge) + minAge;
                break;
        }

        return new Event(eventID, currentUser.getUsername(), p.getPersonID(), location.getLatitude(),
                location.getLongitude(), location.getCountry(), location.getCity(), eventType, year);
    }

    /**
     * Default version of the createEvent function. Sets yearData to an impossible year (not 0, since that's valid).
     * As of now, the only use for this function is when I need to set 'marriage' or 'death, but I'll keep it this
     * way since there ought to be a possibility of creating events besides those two (i.e., 'graduation')
     * @param eventType the specified eventType
     * @param p the person of the event
     * @return the new event
     */
    private Event createEvent(String eventType, Person p) {
        return createEvent(eventType, p, -999999);
    }

    /**
     * generates parents for each person in currentGenerationPeople
     * @return the new parents
     */
    private List<Person> createParents() {
        List<Person> newParents = new ArrayList<>();
        for (Person p : currentGenerationPeople) {
            Person father = createPerson("m");
            Person mother = createPerson("f");
            father.setSpouseID(mother.getPersonID());
            mother.setSpouseID(father.getPersonID());
            p.setFatherID(father.getPersonID());
            p.setMotherID(mother.getPersonID());
            newParents.add(father);
            newParents.add(mother);
        }
        return newParents;
    }

    /**
     * creates a new person, m or f, depending on the specified gender
     * @param gender the specified gender
     * @return the new person
     */
    private Person createPerson(String gender) {
        String firstName = "";
        if (gender.equals("m")) {
            firstName = mnames.pickRandomName();
        }
        else if (gender.equals("f")) {
            firstName = fnames.pickRandomName();
        }
        String lastName = snames.pickRandomName();
        String personID = UUID.randomUUID().toString();
        return new Person(personID, currentUser.getUsername(), firstName, lastName, gender, null, null, null);
    }
}
