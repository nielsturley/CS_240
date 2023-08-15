package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import request.AllPersonRequest;
import request.PersonRequest;
import result.AllPersonResult;
import result.PersonResult;
import service.AllPersonService;
import service.PersonService;

import java.io.*;
import java.net.HttpURLConnection;

public class PersonHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            //check for 'get' request method
            if (!(exchange.getRequestMethod().equalsIgnoreCase("get"))) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                PersonResult result = new PersonResult("Error: Invalid request");
                createResponseBody(result, exchange);
            } else {
                Headers reqHeaders = exchange.getRequestHeaders();
                //check for 'Authorization' key
                if (!(reqHeaders.containsKey("Authorization"))) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    PersonResult result = new PersonResult("Error: Invalid request");
                    createResponseBody(result, exchange);
                } else {
                    String authToken = reqHeaders.getFirst("Authorization");
                    InputStream reqBody = exchange.getRequestBody();
                    String reqData = readString(reqBody);

                    //divide the input urlpath
                    String urlPath = exchange.getRequestURI().toString();
                    String[] strings = urlPath.split("/", 3);
                    String personID = null;
                    if (strings.length == 3) { //if there is a specified personID
                        personID = strings[2];
                    }

                    if (personID != null) { //this is a PersonRequest
                        //display the input on system
                        System.out.println("\nPerson request:");
                        System.out.println("url: " + exchange.getRequestURI().toString());
                        System.out.println("request body:");
                        System.out.println(reqData);

                        //begin PersonService
                        PersonRequest request = new PersonRequest(personID, authToken);
                        PersonService service = new PersonService();
                        PersonResult result = service.getPerson(request);

                        if (result.isSuccess()) {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                            createResponseBody(result, exchange);
                            System.out.println("Successfully completed Person request");
                        } else {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                            createResponseBody(result, exchange);
                        }
                    } else { //this is a AllPersonRequest
                        //display the input on system
                        System.out.println("\nAllPerson request:");
                        System.out.println("url: " + exchange.getRequestURI().toString());
                        System.out.println("request body:");
                        System.out.println(reqData);

                        //begin AllPersonService
                        AllPersonRequest request = new AllPersonRequest(authToken);
                        AllPersonService service = new AllPersonService();
                        AllPersonResult result = service.getAllPeople(request);

                        if (result.isSuccess()) {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                            createResponseBody(result, exchange);
                            System.out.println("Successfully completed AllPerson request");
                        } else {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                            createResponseBody(result, exchange);
                        }
                    }
                }
            }
        } catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            e.printStackTrace();
            PersonResult result = new PersonResult("Error: Internal server error");
            createResponseBody(result, exchange);
        }
    }

    //overloaded for AllPersonResult
    private void createResponseBody(AllPersonResult result, HttpExchange exchange) throws IOException {
        //display output on system
        System.out.println("response body:");
        System.out.println(result.toString());

        //create and close response body
        OutputStream resBody = exchange.getResponseBody();
        Gson gson1 = new GsonBuilder().create();
        String resultString = gson1.toJson(result);
        writeString(resultString, resBody);
        resBody.close();
    }

    //overloaded for PersonResult
    private void createResponseBody(PersonResult result, HttpExchange exchange) throws IOException {
        //display output on system
        System.out.println("response body:");
        System.out.println(result.toString());

        //create and close response body
        OutputStream resBody = exchange.getResponseBody();
        Gson gson1 = new GsonBuilder().create();
        String resultString = gson1.toJson(result);
        writeString(resultString, resBody);
        resBody.close();
    }

    private String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}

