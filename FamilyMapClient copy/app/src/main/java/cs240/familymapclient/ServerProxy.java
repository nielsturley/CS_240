package cs240.familymapclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import request.AllEventRequest;
import request.AllPersonRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.AllEventResult;
import result.AllPersonResult;
import result.LoginResult;
import result.RegisterResult;


public class ServerProxy {
    String serverHost;
    String serverPort;

    public ServerProxy(String serverHost, String serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    //logs in with the provided request. Returns login result with login success status.
    public LoginResult login(LoginRequest request) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");
            System.out.println(url);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);

            http.connect();

            Gson gson = new GsonBuilder().create();
            String reqData = gson.toJson(request);
            System.out.println(reqData);
            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Success");
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                System.out.println(respData);
                Gson gson1 = new Gson();
                return gson1.fromJson(respData, LoginResult.class);
            }
            else {
                System.out.println("Error: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                System.out.println(respData);
                Gson gson1 = new Gson();
                return gson1.fromJson(respData, LoginResult.class);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new LoginResult("Error: Exception thrown while logging in");
        }
    }

    //registers and logs in with the provided request. Returns register result with success status.
    public RegisterResult register(RegisterRequest request) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");
            System.out.println(url);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);

            http.connect();

            Gson gson = new GsonBuilder().create();
            String reqData = gson.toJson(request);
            System.out.println(reqData);
            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Success");
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                System.out.println(respData);
                Gson gson1 = new Gson();
                return gson1.fromJson(respData, RegisterResult.class);
            }
            else {
                System.out.println("Error: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                System.out.println(respData);
                Gson gson1 = new Gson();
                return gson1.fromJson(respData, RegisterResult.class);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new RegisterResult("Error: Exception thrown while registering user");
        }
    }

    //grabs all people for a specific user. returns AllPersonResult with success status.
    public AllPersonResult getPeople(AllPersonRequest request) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");
            System.out.println(url);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", request.getAuthtoken());

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Success");
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                System.out.println(respData);
                Gson gson1 = new Gson();
                return gson1.fromJson(respData, AllPersonResult.class);
            }
            else {
                System.out.println("Error: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                System.out.println(respData);
                Gson gson1 = new Gson();
                return gson1.fromJson(respData, AllPersonResult.class);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new AllPersonResult("Error: Exception thrown while logging in");
        }
    }

    //grabs all events for a specific user. returns AllEventResult with success status.
    public AllEventResult getEvents(AllEventRequest request) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");
            System.out.println(url);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", request.getAuthtoken());

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Success");
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                System.out.println(respData);
                Gson gson1 = new Gson();
                return gson1.fromJson(respData, AllEventResult.class);
            }
            else {
                System.out.println("Error: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                System.out.println(respData);
                Gson gson1 = new Gson();
                return gson1.fromJson(respData, AllEventResult.class);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new AllEventResult("Error: Exception thrown while logging in");
        }
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

    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

}
