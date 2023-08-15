package cs240.familymapclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import model.Event;
import model.Person;
import model.User;
import request.AllEventRequest;
import request.AllPersonRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.AllEventResult;
import result.AllPersonResult;
import result.LoginResult;
import result.RegisterResult;


public class ServerProxyTest {
    private ServerProxy proxy;
    private final User testUser1 = new User("sheila", "parker", "sheila@parker.com",
            "Sheila", "Parker", "f", null);
    private final User testUser2 = new User("username", "password", "iam@home.com",
            "Bob", "Builder", "m", null);
    private String authToken;
    private List<Person> testPeopleData;
    private List<Event> testEventData;

    @Before
    public void setUp() {
        System.out.println("\n\n~~~SETTING UP FOR THE NEXT TEST~~~\n~~~Don't mind me~~~");
        proxy = new ServerProxy("localhost", "8080");
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
        authToken = registerResult.getAuthtoken();
        testUser1.setPersonID(registerResult.getPersonID());

        AllPersonRequest allPersonRequest = new AllPersonRequest(authToken);
        AllPersonResult allPersonResult = proxy.getPeople(allPersonRequest);
        testPeopleData = allPersonResult.getData();

        AllEventRequest allEventRequest = new AllEventRequest(authToken);
        AllEventResult allEventResult = proxy.getEvents(allEventRequest);
        testEventData = allEventResult.getData();
        System.out.println("~~~~~~");
    }

    @Test
    public void loginPass() {
        System.out.println("\n\n***BEGINNING LOGIN PASS TEST***");

        LoginRequest loginRequest = new LoginRequest(testUser1.getUsername(), testUser1.getPassword());
        LoginResult loginResult = proxy.login(loginRequest);

        assertNotNull(loginResult);
        assertTrue(loginResult.isSuccess());
        assertEquals(loginResult.getPersonID(), testUser1.getPersonID());
        System.out.println("******");
    }

    @Test
    public void loginFail() {
        System.out.println("\n\n***BEGINNING LOGIN FAIL TEST***");

        //bad login credentials
        LoginRequest loginRequest = new LoginRequest("bad_username", "bad_password");
        LoginResult loginResult = proxy.login(loginRequest);

        assertNotNull(loginResult);
        assertFalse(loginResult.isSuccess());
        System.out.println("******");
    }

    @Test
    public void registerPass() {
        System.out.println("\n\n***BEGINNING REGISTER PASS TEST***");
        RegisterRequest registerRequest = new RegisterRequest(testUser2.getUsername(), testUser2.getPassword(),
                testUser2.getEmail(), testUser2.getFirstName(), testUser2.getLastName(), testUser2.getGender());
        RegisterResult registerResult = proxy.register(registerRequest);

        assertNotNull(registerResult);
        assertTrue(registerResult.isSuccess());
        System.out.println("******");
    }

    @Test
    public void registerFail() {
        System.out.println("\n\n***BEGINNING REGISTER FAIL TEST***");
        //person already registered
        RegisterRequest registerRequest = new RegisterRequest(testUser1.getUsername(), testUser1.getPassword(),
                testUser1.getEmail(), testUser1.getFirstName(), testUser1.getLastName(), testUser1.getGender());
        RegisterResult registerResult = proxy.register(registerRequest);

        assertNotNull(registerResult);
        assertFalse(registerResult.isSuccess());
        System.out.println("******");
    }

    @Test
    public void getPeoplePass() {
        System.out.println("\n\n***BEGINNING GET PEOPLE PASS TEST***");

        AllPersonRequest allPersonRequest = new AllPersonRequest(authToken);
        AllPersonResult allPersonResult = proxy.getPeople(allPersonRequest);

        assertNotNull(allPersonResult);
        assertTrue(allPersonResult.isSuccess());
        assertNotNull(allPersonResult.getData());

        List<Person> comparePeopleData = allPersonResult.getData();

        //remove all people that are the same, aka check that testPeopleData = comparePeopleData
        testPeopleData.retainAll(comparePeopleData);
        comparePeopleData.removeAll(testPeopleData);
        assertEquals(comparePeopleData.size(), 0);
        System.out.println("******");
    }

    @Test
    public void getPeopleFail() {
        System.out.println("\n\n***BEGINNING GET PEOPLE FAIL TEST***");
        AllPersonRequest allPersonRequest = new AllPersonRequest("bad_authtoken");
        AllPersonResult allPersonResult = proxy.getPeople(allPersonRequest);

        assertFalse(allPersonResult.isSuccess());
        System.out.println("******");
    }

    @Test
    public void getEventsPass() {
        System.out.println("\n\n***BEGINNING GET EVENTS PASS TEST***");
        AllEventRequest allEventRequest = new AllEventRequest(authToken);
        AllEventResult allEventResult = proxy.getEvents(allEventRequest);

        assertNotNull(allEventResult);
        assertTrue(allEventResult.isSuccess());
        assertNotNull(allEventResult.getData());

        List<Event> compareEventData = allEventResult.getData();

        //remove all events that are the same, aka check that testEventData = compareEventData
        testEventData.retainAll(compareEventData);
        compareEventData.removeAll(testEventData);
        assertEquals(compareEventData.size(), 0);
        System.out.println("******");
    }

    @Test
    public void getEventsFail() {
        System.out.println("\n\n***BEGINNING GET PEOPLE FAIL TEST***");
        AllEventRequest allEventRequest = new AllEventRequest("bad_authtoken");
        AllEventResult allEventResult = proxy.getEvents(allEventRequest);

        assertFalse(allEventResult.isSuccess());
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