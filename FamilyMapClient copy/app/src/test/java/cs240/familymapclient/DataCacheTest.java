package cs240.familymapclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import model.Event;
import model.Person;
import model.User;
import request.AllEventRequest;
import request.AllPersonRequest;
import request.RegisterRequest;
import result.AllEventResult;
import result.AllPersonResult;
import result.RegisterResult;

public class DataCacheTest {
    private final static DataCache dataCache = DataCache.getInstance();
    private static List<Person> testPeopleData;
    private static List<Event> testEventData;
    private static final User testUser1 = new User("sheila", "parker", "sheila@parker.com",
            "Sheila", "Parker", "f", null);
    private static Person testPerson;

    @Before
    public void setUp() {
        dataCache.getSettings().resetSettings();
    }

    @BeforeClass
    public static void setUpAll() {
        System.out.println("\n\n~~~SETTING UP FOR ALL THE TESTS~~~\n~~~Don't mind me~~~");
        ServerProxy proxy = new ServerProxy("localhost", "8080");

        try {
            URL url = new URL("http://localhost:8080/clear");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.connect();

            if (http.getResponseCode() != HttpURLConnection.HTTP_OK) {
                fail("Error: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                System.out.println(respData);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        RegisterRequest registerRequest = new RegisterRequest(testUser1.getUsername(), testUser1.getPassword(),
                testUser1.getEmail(), testUser1.getFirstName(), testUser1.getLastName(), testUser1.getGender());
        RegisterResult registerResult = proxy.register(registerRequest);

        if (!registerResult.isSuccess()) {
            fail("Unable to set up register test user");
        }

        String authToken = registerResult.getAuthtoken();
        testUser1.setPersonID(registerResult.getPersonID());
        dataCache.setCurrentUserID(testUser1.getPersonID());

        AllPersonRequest allPersonRequest = new AllPersonRequest(authToken);
        AllPersonResult allPersonResult = proxy.getPeople(allPersonRequest);
        testPeopleData = allPersonResult.getData();

        AllEventRequest allEventRequest = new AllEventRequest(authToken);
        AllEventResult allEventResult = proxy.getEvents(allEventRequest);
        testEventData = allEventResult.getData();

        dataCache.insertPeopleEventData(testPeopleData, testEventData);
        testPerson = dataCache.getPersonByID(testUser1.getPersonID());

        System.out.println("~~~~~~");
    }

    @Test
    public void familyRelationshipsPass() {
        System.out.println("\n\n***BEGINNING FAMILY RELATIONSHIPS PASS TEST***");
        Person person = dataCache.getPersonByID(testUser1.getPersonID());
        Person mother = dataCache.getPersonByID(person.getMotherID());
        Person father = dataCache.getPersonByID(person.getFatherID());

        assertEquals("Mother", dataCache.getRelationship(person, mother));
        assertEquals("Father", dataCache.getRelationship(person, father));
        assertEquals("Child", dataCache.getRelationship(mother, person));
        assertEquals("Child", dataCache.getRelationship(father, person));
        assertEquals("Spouse", dataCache.getRelationship(mother, father));
        assertEquals("Spouse", dataCache.getRelationship(father, mother));
        System.out.println("******");
    }

    @Test
    public void familyRelationshipsFail() {
        System.out.println("\n\n***BEGINNING FAMILY RELATIONSHIPS FAIL TEST***");
        Person person = dataCache.getPersonByID(testUser1.getPersonID());
        Person randomGuy = new Person("uh_oh_id", null, null,
                null, null, "not_yo_daddy", "not_yo_mommy", "not_yo_spouse");

        assertEquals("ERROR! No relationship found!", dataCache.getRelationship(person, randomGuy));
        assertEquals("ERROR! No relationship found!", dataCache.getRelationship(randomGuy, person));
        System.out.println("******");
    }

    @Test
    public void filteredSettingsPass() {
        System.out.println("\n\n***BEGINNING FILTERED SETTINGS PASS TEST***");
        Settings settings = dataCache.getSettings();

        //all settings are default to on
        List<Event> motherEvents = dataCache.getPersonEvents(testPerson.getMotherID());
        assertNotNull(motherEvents);
        assertTrue(motherEvents.size() > 0);
        for (Event e : motherEvents) {
            assertEquals(e.getPersonID(), testPerson.getMotherID());
        }

        List<Event> fatherEvents = dataCache.getPersonEvents(testPerson.getFatherID());
        assertNotNull(fatherEvents);
        assertTrue(fatherEvents.size() > 0);
        for (Event e : fatherEvents) {
            assertEquals(e.getPersonID(), testPerson.getFatherID());
        }


        //setting filters to FALSE means that they are OFF (aka, setMaleFilter(false) means no males)

        //test male filter
        settings.setMaleFilter(false);
        dataCache.updateDisplayed();
        for (Event e : fatherEvents) {
            assertFalse(dataCache.isEventOnDisplay(e));
        }
        for (Event e : motherEvents) {
            assertTrue(dataCache.isEventOnDisplay(e));
        }

        settings.resetSettings();

        //test female filter
        settings.setFemaleFilter(false);
        dataCache.updateDisplayed();
        for (Event e : fatherEvents) {
            assertTrue(dataCache.isEventOnDisplay(e));
        }
        for (Event e : motherEvents) {
            assertFalse(dataCache.isEventOnDisplay(e));
        }

        settings.resetSettings();

        //test paternal filter
        settings.setPaternalSideFilter(false);
        dataCache.updateDisplayed();
        for (Event e : fatherEvents) {
            assertFalse(dataCache.isEventOnDisplay(e));
        }
        for (Event e : motherEvents) {
            assertTrue(dataCache.isEventOnDisplay(e));
        }

        settings.resetSettings();

        //test maternal filter
        settings.setMaternalSideFilter(false);
        dataCache.updateDisplayed();
        for (Event e : fatherEvents) {
            assertTrue(dataCache.isEventOnDisplay(e));
        }
        for (Event e : motherEvents) {
            assertFalse(dataCache.isEventOnDisplay(e));
        }


        System.out.println("******");
    }

    @Test
    public void filteredSettingsFail() {
        System.out.println("\n\n***BEGINNING FILTERED SETTINGS FAIL TEST***");
        Settings settings = dataCache.getSettings();

        settings.setMaleFilter(false);
        settings.setFemaleFilter(false);
        settings.setMaternalSideFilter(false);
        settings.setPaternalSideFilter(false);
        dataCache.updateDisplayed();

        List<Event> testEvents;
        for (Person p : testPeopleData) {
            testEvents = dataCache.getPersonEvents(p.getPersonID());
            for (Event e : testEvents) {
                assertFalse(dataCache.isEventOnDisplay(e));
            }
        }

        System.out.println("******");
    }

    @Test
    public void sortedEventsPass() {
        System.out.println("\n\n***BEGINNING SORTED EVENTS PASS TEST***");
        Person person = dataCache.getCurrentUserPerson();
        List<Event> events = dataCache.getPersonEvents(person.getMotherID());
        assertNotNull(events);
        assertEquals(events.get(0).getEventType(), "Birth");
        assertEquals(events.get(events.size() - 1).getEventType(), "Death");
        int compareYear = 0;
        for (Event e : events) {
            assertTrue(compareYear < e.getYear());
            compareYear = e.getYear();
        }
        System.out.println("******");
    }

    @Test
    public void sortedEventsFail() {
        System.out.println("\n\n***BEGINNING SORTED EVENTS FAIL TEST***");
        List<Event> events = dataCache.getPersonEvents("bad_person_id");
        assertNull(events);
        System.out.println("******");
    }

    @Test
    public void searchPeoplePass() {
        System.out.println("\n\n***BEGINNING SEARCH PEOPLE PASS TEST***");
        String query = "SHEILA";
        List<Person> people = dataCache.searchPeople(query);

        List<Person> testPeople = new ArrayList<>();
        query = query.toLowerCase();
        for (Person p : testPeopleData) {
            String fname = p.getFirstName().toLowerCase();
            String lname = p.getLastName().toLowerCase();
            if (fname.contains(query) || lname.contains(query)) {
                testPeople.add(p);
            }
        }

        //removes all people that are the same, aka checks that testPeople = people
        testPeople.retainAll(people);
        people.removeAll(testPeople);
        assertEquals(people.size(), 0);
        System.out.println("******");
    }

    @Test
    public void searchPersonFail() {
        System.out.println("\n\n***BEGINNING SEARCH PERSON FAIL TEST***");
        String query = "bad_search_query";
        List<Person> people = dataCache.searchPeople(query);

        assertEquals(people.size(), 0);
        System.out.println("******");
    }

    @Test
    public void searchEventsPass() {
        System.out.println("\n\n***BEGINNING SEARCH EVENTS PASS TEST***");
        String query = "UNITED STATES";
        List<Event> events = dataCache.searchEvents(query);

        List<Event> testEvents = new ArrayList<>();
        query = query.toLowerCase();
        for (Event e : testEventData) {
            String country = e.getCountry().toLowerCase();
            String city = e.getCity().toLowerCase();
            String eventType = e.getEventType().toLowerCase();
            String year = String.valueOf(e.getYear());
            if (country.contains(query) || city.contains(query) ||
                    eventType.contains(query) || year.contains(query)) {
                testEvents.add(e);
            }
        }

        //removes all events that are the same, aka checks that testEvents = events
        testEvents.retainAll(events);
        events.removeAll(testEvents);
        assertEquals(events.size(), 0);
        System.out.println("******");
    }

    @Test
    public void searchEventsFail() {
        System.out.println("\n\n***BEGINNING SEARCH EVENTS FAIL TEST***");
        String query = "bad_search_query";
        List<Event> events = dataCache.searchEvents(query);

        assertEquals(events.size(), 0);
        System.out.println("******");
    }

    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }
}
